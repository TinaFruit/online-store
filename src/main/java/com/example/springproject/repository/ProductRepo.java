package com.example.springproject.repository;

import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

//display getmapping 商品展示  // 数据库： 分页 + 索引优化
//remove get 商品下架  // 数据库： 软删除 / 上下架状态
//search post 搜索商品  // 数据库： like / 分词 / 索引 商品名字/id
//add post 添加商品 // 数据库：管理员权限
//update put 更新商品   // 数据库：DTO + 更新流程

@Repository
public class ProductRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> disaplayRepo(int page, int size){
        String sql ="select * from products limit ? offset ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql,size,page);
        return maps;
    }

    public boolean productAddRepo(ProductAdminDTO productAdminDTO){
        String sql ="insert into products (product_name, description,price, stock_quantity,category,image_url,status, created_at, updated_at, seller_id) values(?,?,?,?,?,?,?,now(),now(),?)";
        String productName = productAdminDTO.getProductName();
        String description = productAdminDTO.getDescription();
        BigDecimal price = productAdminDTO.getPrice();
        Integer stockQuantity = productAdminDTO.getStockQuantity();
        String category = productAdminDTO.getCategory();
        String imageUrl = productAdminDTO.getImageUrl();
        String status = productAdminDTO.getStatus();
        Long sellerId = productAdminDTO.getSellerId();
        LocalDateTime.now();   // created_at
                LocalDateTime.now();   // updated_at

        int update = jdbcTemplate.update(sql, productName, description, price, stockQuantity, category, imageUrl, status, sellerId);
        return update>0;
    }
    public boolean productRemoveRepo(Long id) {
        String sql = "delete from products where id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    public boolean productUpdaterepo(Long id,BigDecimal price){
        String sql ="update products set price = ? where id = ? ";
        int rows = jdbcTemplate.update(sql,price, id);
        return rows > 0;
    }

    public ProductSummaryDTO productSearchrepo(Long id){
        ProductSummaryDTO productSummaryDTO = null;
        try {
            String sql ="select * from products where id = ?";
            productSummaryDTO = jdbcTemplate.queryForObject(sql, (resultSet, rownum) -> {
                ProductSummaryDTO dto = new ProductSummaryDTO(
                        resultSet.getLong("id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("description"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getString("category"),
                        resultSet.getString("image_url")
                );
                return dto;
            }, id);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return productSummaryDTO;
    }


}
