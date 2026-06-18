package com.example.springproject.mapper;

import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Mapper
public interface ProductMapper {

    List<Map<String, Object>> mapss(@Param("size") int size, @Param("page") int page);
    // 一个普通参数 → MyBatis 不知道叫什么名字 → 需要 @Param 告诉它
    ProductSummaryDTO productSearch(@Param("id") Long id);

    // 一个对象 → MyBatis 直接从对象的 getter 找 → 不需要 @Param
    int addProduct(ProductAdminDTO productAdminDTO);
    int delete(@Param("id") Long id);
    int update(@Param("id") Long id,@Param("price") BigDecimal price);
}