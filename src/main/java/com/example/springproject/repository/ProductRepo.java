package com.example.springproject.repository;

import com.example.springproject.exeption.AppException;
import com.example.springproject.mapper.ProductMapper;
import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public ProductMapper productMapper;

    public List<Map<String, Object>> displayRepo(int page, int size){
//        String sql ="select * from products limit ? offset ?";
//        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql,size,page);
        List<Map<String, Object>> mapss = productMapper.mapss(size, page);
        return mapss;
    }

    public boolean productAddRepo(ProductAdminDTO productAdminDTO){
//        String sql ="insert into products (product_name, description,price, stock_quantity,category,image_url,status, created_at, updated_at, seller_id) values(?,?,?,?,?,?,?,now(),now(),?)";
//        String productName = productAdminDTO.getProductName();
//        String description = productAdminDTO.getDescription();
//        BigDecimal price = productAdminDTO.getPrice();
//        Integer stockQuantity = productAdminDTO.getStockQuantity();
//        String category = productAdminDTO.getCategory();
//        String imageUrl = productAdminDTO.getImageUrl();
//        String status = productAdminDTO.getStatus();
//        Long sellerId = productAdminDTO.getSellerId();
//        LocalDateTime.now();   // created_at
//                LocalDateTime.now();   // updated_at

//        int update = jdbcTemplate.update(sql, productName, description, price, stockQuantity, category, imageUrl, status, sellerId);
        int update = productMapper.addProduct(productAdminDTO);

        return update>0;
    }
    public boolean productRemoveRepo(Long id) {
//        String sql = "delete from products where id = ?";
//        int rows = jdbcTemplate.update(sql, id);
        int delete = productMapper.delete(id);
        if(delete<=0) throw new AppException(403,"forbidden, fialed to delete");
        return true;
    }

    public boolean productUpdaterepo(Long id,BigDecimal price){
//        String sql ="update products set price = ? where id = ? ";
//        int rows = jdbcTemplate.update(sql,price, id);
        int update = productMapper.update(id, price);
        if(update<=0) throw new AppException(403,"forbidden, failed to update");

        return true;
    }

    public ProductSummaryDTO productSearchrepo(Long id){
//            String sql ="select * from products where id = ?";
//        ProductSummaryDTO productSummaryDTO = jdbcTemplate.queryForObject(sql, (resultSet, rownum) -> {
//                ProductSummaryDTO dto = new ProductSummaryDTO(
//                        resultSet.getLong("id"),
//                        resultSet.getString("product_name"),
//                        resultSet.getString("description"),
//                        resultSet.getBigDecimal("price"),
//                        resultSet.getString("category"),
//                        resultSet.getString("image_url")
//                );
//                return dto;
//            }, id);

        ProductSummaryDTO productSummaryDTO = productMapper.productSearch(id);
        if (productSummaryDTO == null) throw new AppException(404, "商品不存在");
        return productSummaryDTO;
    }


}
