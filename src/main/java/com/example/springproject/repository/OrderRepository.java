package com.example.springproject.repository;

import com.example.springproject.model.OrderDetailDTO;
import com.example.springproject.model.ProductAdminDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

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

    //追加订单 1 业务逻辑可选
    //退货模块 1 状态流转
    //更新订单 1 状态更新、事务
    //查询订单 1 普通用户仅自己订单，管理员查看所有
    @Transactional
    public boolean amendentRepo(List<HashMap<String, Integer>> products, int orderId) {  // productId + quantity +order id
        //orderDTO
        //  验证管理员权限）
        //1检查订单存在 --》404 no found
        //2.order status should be "PENDING", OTHERWISE not allowed
        //3. stock = stock + ?
        //4. order_detail + order

        //1 检查订单存在 + 2.check "pending" ---有status的话，就一定存在
        String sql ="select status from neworders where order_id = ?";
        String status;
        try{
            status=jdbcTemplate.queryForObject(sql, String.class, orderId);
        }catch (EmptyResultDataAccessException e){
            throw new RuntimeException("404 NO FOUND");
        }
        // 2.2check "pending"
        if(!status.equals("PENDING")){throw new IllegalStateException("the order is not pending, the status is "+status);}

        //get quantity, productid and total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (HashMap<String, Integer> product: products){
            Integer productid = product.get("product_id");
            Integer quantity = product.get("quantity");
            String sqln = "select price from products where id = ?";
            try{
            BigDecimal singlePrice = jdbcTemplate.queryForObject(sqln, BigDecimal.class, productid);
            //get total price
            totalPrice = totalPrice.add(singlePrice.multiply(BigDecimal.valueOf(quantity)));

            }catch (Exception e){
                throw  new RuntimeException("failed to update stock_quantity");
            }

            // 3. stock = stock -? ?
            String sql2 ="update products set stock_quantity = stock_quantity - ? where id = ? and stock_quantity >= ?";
            int update = jdbcTemplate.update(sql2, quantity, productid,quantity); //注意防止超卖 and stock_quantity > ?"
            if(update <=0 ){throw new RuntimeException("failed to update");}

            //update neworder_detail table
            String sql4 ="update neworder_detail set quantity = quantity+?, updated_at = now() where order_id = ? and product_id = ?";
            int update1 = jdbcTemplate.update(sql4, quantity, orderId, productid);
            //⚠️：如果update1 = 0 可能是新产品，需要添加
            if(update1 == 0){
                String sql5 ="insert into neworder_detail (order_id, product_id, quantity, created_at,updated_at)values (?, ?, ?, now(), now())";
                int update2 = jdbcTemplate.update(sql5, orderId, productid,quantity);
                if(update2 <=0 ){throw new RuntimeException("failed to insert");}
            }

        }
        // 4. order+ order_detail
        String sql3 ="update neworders set total_price = total_price + ? where order_id = ? ";
        int update = jdbcTemplate.update(sql3, totalPrice, orderId);
        if(update <=0){throw new RuntimeException("failed to update");}

        return true;

    }

    @Transactional
    public boolean returnProductsRepo(int orderId) {

        // 1. 检查订单存在 + 拿当前状态
        String sql = "select status from neworders where order_id = ?";
        String status;
        try {
            status = jdbcTemplate.queryForObject(sql, String.class, orderId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        // 2. 只有已支付/已发货/已完成的订单才能退货
        if (!Set.of("PAID", "SHIPPED", "COMPLETED").contains(status)) {
            throw new IllegalStateException("Order cannot be returned, current status: " + status);
        }

        // 3. 恢复库存 —— 遍历该订单的所有商品明细
        String detailSql = "select product_id, quantity from neworder_detail where order_id = ?";
        List<Map<String, Object>> details = jdbcTemplate.queryForList(detailSql, orderId);

        for (Map<String, Object> detail : details) {
            Integer productId = (Integer) detail.get("product_id");
            Integer quantity = (Integer) detail.get("quantity");

            String stockSql = "update products set quantity_stock = quantity_stock + ? where id = ?";
            int stockUpdated = jdbcTemplate.update(stockSql, quantity, productId);
            if (stockUpdated <= 0) {
                throw new IllegalStateException("Failed to restore stock for product: " + productId);
            }
        }

        // 4. 更新订单状态为RETURNED
        String updateSql = "update neworders set status = 'RETURNED', updated_at = now() where order_id = ?";
        int updated = jdbcTemplate.update(updateSql, orderId);
        if (updated <= 0) {
            throw new IllegalStateException("Failed to update order status");
        }

        return true;
    }

    @Transactional
    public boolean updateOrdersRepo(int orderId, String newStatus) {

        String sql = "select status from neworders where order_id = ?";
        String currentStatus;
        try {
            currentStatus = jdbcTemplate.queryForObject(sql, String.class, orderId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        Map<String, Set<String>> allowedTransitions = Map.of(
                "PENDING", Set.of("PAID"),
                "PAID", Set.of("SHIPPED"),
                "SHIPPED", Set.of("COMPLETED")
        );

        if (!allowedTransitions.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot change status from " + currentStatus + " to " + newStatus);
        }

        String updateSql = "update neworders set status = ?, updated_at = now() where order_id = ?";
        int updated = jdbcTemplate.update(updateSql, newStatus, orderId);
        if (updated <= 0) {
            throw new IllegalStateException("Failed to update order status");
        }

        return true;
    }

    public  List<OrderDetailDTO> searchOrderRepo(String username) { //based on user id , order inner join order details
        //get userid by username
        String sql1 = "select id from users where user_name = ? ";
        Integer userId = jdbcTemplate.queryForObject(sql1, Integer.class, username);

        //1.check order existence --> userid
        String sql = "select n.order_id, n.user_id, n.total_price, n.status, " +
                "n.created_at as order_created_at, " +
                "d.product_id, d.product_name, d.price, d.image_url, d.quantity, " +
                "d.created_at as detail_created_at " +
                "from neworders n join neworder_detail d on n.order_id = d.order_id " +
                "where n.user_id = ?";
        List<OrderDetailDTO> orderlist ;
        try {
            orderlist = jdbcTemplate.query(sql, (rs, row) -> {
                OrderDetailDTO dto = new OrderDetailDTO();

                dto.setOrderId(rs.getInt("order_id"));
                dto.setUserId(rs.getInt("user_id"));
                dto.setTotalPrice(rs.getBigDecimal("total_price"));
                dto.setStatus(rs.getString("status"));

                Timestamp orderCreated = rs.getTimestamp("order_created_at");
                dto.setOrderCreatedAt(orderCreated != null ? orderCreated.toLocalDateTime() : null);

                dto.setProductId(rs.getInt("product_id"));
                dto.setProductName(rs.getString("product_name"));
                dto.setPrice(rs.getBigDecimal("price"));
                dto.setImageUrl(rs.getString("image_url"));
                dto.setQuantity(rs.getInt("quantity"));

                Timestamp detailCreated = rs.getTimestamp("detail_created_at");
                dto.setDetailCreatedAt(detailCreated != null ? detailCreated.toLocalDateTime() : null);

                return dto;
            }, userId);
        } catch (Exception e) {
            throw new RuntimeException("failed to search orders", e);
        }
        //2.display all detals
       return orderlist;
    }
}
