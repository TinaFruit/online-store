package com.example.springproject.repository;

import com.example.springproject.model.ProductAdminDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {
    @Autowired
    public JdbcTemplate jdbcTemplate;


    //下单 1 事务 + 乐观锁防止超卖 + keyholder
//    逻辑: 1.检查库存,有->扣库存->算数量X价格=总价 -》过程中 获得products表中一些数据给后面下单的表格使用
//          2.开始下单，先加入到neworders, 用keyholder得到order_id（在数据库中自增）
//           3.再加入到neworders_detail

    //删除订单 1 管理员权限
    //追加订单 1 业务逻辑可选
    //退货模块 1 状态流转
    //更新订单 1 状态更新、事务
    //查询订单 1 普通用户仅自己订单，管理员查看所有

    @Transactional//@Transactional 保证要么全部成功，要么全部回滚
    public boolean putOrderRepo(List<HashMap<String, Integer>> products, Integer userId) {
        //START
        BigDecimal totalPrice = BigDecimal.ZERO;

        List<ProductAdminDTO> productList = new ArrayList<>();
        List<Integer> quantityList = new ArrayList<>();
        for (HashMap<String, Integer> singleProduct : products) {
            Integer product_id = singleProduct.get("product_id");
            Integer quantity = singleProduct.get("quantity");
            String sql = "select id,stock_quantity,product_name,price,image_url from products where id = ? ";
// ✅ 多个字段要用 RowMapper
            ProductAdminDTO productAdminDTO = jdbcTemplate.queryForObject(sql, (rs, row) -> {
                ProductAdminDTO dto = new ProductAdminDTO();
                dto.setId(rs.getLong("id"));
                dto.setProductName(rs.getString("product_name"));
                dto.setPrice(rs.getBigDecimal("price"));
                dto.setImageUrl(rs.getString("image_url"));
                dto.setStockQuantity(rs.getInt("stock_quantity"));
                return dto;
            }, product_id);
            Integer product_stock = productAdminDTO.getStockQuantity();
            if (product_stock < quantity) {
                String str = product_id + ":库存不足, 只剩下:" + product_stock;
                throw new RuntimeException(str);
            }

            // 库存都够了，开始扣库存
            jdbcTemplate.update(
                    "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?",
                    quantity, product_id, quantity
            );// 担心还没下单，库存就扣了 → @Transactional 检测到异常 → 自动 rollback → 商品1 的扣库存也撤销了 ✅

            BigDecimal price = productAdminDTO.getPrice();
            //get total price
            totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(quantity)));

            productList.add(productAdminDTO);
            quantityList.add(quantity);
        }
        // 库存都够了，开始下单


        //1.add neworders and  get its order_id by using keyholder
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql1 = "insert into neworders (user_id,total_price,deliver_date) values(?,?,date_add(now(),interval 7 day))";
        BigDecimal finalTotalPrice = totalPrice; //← 循环结束后不再变了，存一份
        //get order_id
        jdbcTemplate.update(x -> {
            PreparedStatement ps = x.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setBigDecimal(2, finalTotalPrice);
            return ps;
        }, keyHolder);
        long orderId = keyHolder.getKey().longValue();

        if (orderId < 0 || orderId == 0) throw new RuntimeException("failed to add to neworder_detail table");

        //2.neworder_detail
        for (int i = 0; i < productList.size(); i++) {
            //get product details
            ProductAdminDTO productAdminDTO1 = productList.get(i);
            Long product_id = productAdminDTO1.getId();
            Integer quantity = quantityList.get(i);
            String productName = productAdminDTO1.getProductName();
            BigDecimal price = productAdminDTO1.getPrice();
            String imageUrl = productAdminDTO1.getImageUrl();

            //add valued into neworder_detail
            String sql2 = "insert into neworder_detail(order_id, product_id,quantity,product_name,price,image_url) " +
                    "values(?,?,?,?,?,?)";
            int update2 = jdbcTemplate.update(sql2, orderId, product_id, quantity, productName, price, imageUrl);
            if (update2 < 0 || update2 == 0) throw new RuntimeException("failed to add to neworder_detail table");
        }
        return true;
    }

    @Transactional
    public boolean deleteOrderRepo(int orderId) {
//        （已验证： 验证管理员权限）
        //1.1检查订单存在 --》404 no found
        //1.2    //⚠️建议只允许删除 "pending" 状态的订单
        //2.返回库存 --注意： 如果订单是 已取消 状态，库存可能已经在取消时归还过了，这里再归还就重复了。建议判断：只有库存已被扣减的状态（如待付款、已付款、处理中）才归还
        //3.删除订单 因为有外键，所以得先删掉子表格neworders_detals，然后删掉对应的父表neworders


        //1.检查订单存在
        String sql = "select count(*) from neworders where order_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, orderId);
        if (count <= 0) throw new RuntimeException("404 no found the order orderId");

        // 在检查订单存在之后，加上状态检查
        String sqlStatus = "select status from neworders where order_id = ?";
        String status = jdbcTemplate.queryForObject(sqlStatus, String.class, orderId);
        if (!"PENDING".equals(status)) {
            throw new RuntimeException("只能删除 pending 状态的订单");
        }

        //2.返回库存
        //2.1先从neworder_detail获取product_id 和quantity list<map<string, integer>>
        //2.2再拿着product_id 去products表中更新stock,
        String sql2 = "Select product_id, quantity from neworder_detail where order_id = ?";
        List<ProductAdminDTO> productAdminDTOs = jdbcTemplate.query(sql2,
                (rs, row) -> {
                    ProductAdminDTO dto = new ProductAdminDTO();
                    dto.setId(rs.getLong("product_id"));
                    dto.setStockQuantity(rs.getInt("quantity"));
                    return dto;
                }
                , orderId);
        for(ProductAdminDTO productAdminDTO: productAdminDTOs){
            Long product_id = productAdminDTO.getId();
            Integer stockQuantity = productAdminDTO.getStockQuantity();
            String sql3 = "Update products set stock_quantity = stock_quantity+? where id = ?";
            int update = jdbcTemplate.update(sql3, stockQuantity, product_id);
            if(update <= 0){ throw new RuntimeException("failed to update products");}
        }


        //3.删除订单neworders_detals，然后删掉对应的父表neworders(status)
        //建议只允许删除 "pending" 状态的订单

        String sql4 = "delete from neworder_detail where order_id = ?";
        int newordersDetalsUpdate = jdbcTemplate.update(sql4, orderId);

        String sql5 =  "delete from neworders where order_id = ?";
        int neworderUpdate = jdbcTemplate.update(sql5, orderId);
        if(newordersDetalsUpdate <= 0 || neworderUpdate<= 0 )
        { throw new RuntimeException("failed to delete products");}

        return true;

    }

    public void amendentRepo() {
    }

    public void preturnProductsRepo() {
    }

    public void updateOrdersRepo() {
    }

    public void searchOderRepo() {

    }
}
