package com.example.springproject.service;

import com.example.springproject.exeption.AppException;
import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import com.example.springproject.repository.ProductRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ProductSer {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private StringRedisTemplate redis;
    //查：
    //删
    //存：

    private static final ObjectMapper objectMapper = new ObjectMapper();
    //
    public ProductSummaryDTO parseJson(String json){
         try{
           return objectMapper.readValue(json, ProductSummaryDTO.class);}
         catch(Exception e){
             throw new RuntimeException("parse issue");
         }
    }
    public String toJson(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e){
            throw new RuntimeException("toJson issue");

        }
    }
    public List<ProductSummaryDTO> disaplaySer(int page, int size) {
        //check redis
        String key ="product:"+page+":"+size;
        String cached = redis.opsForValue().get(key);
        if(cached != null){
            try {
                return objectMapper.readValue(cached, objectMapper.getTypeFactory().constructCollectionType(List.class, ProductSummaryDTO.class));
            }catch (Exception e) {
                throw new RuntimeException("cached issue");
            }

        }

        //original codes
        List<Map<String, Object>> maps = productRepo.disaplayRepo(page, size);
        if (maps == null || maps.isEmpty()) throw new AppException(404, "没有商品");
        List<ProductSummaryDTO> productSummaryDTOS = new ArrayList<>();
        for(Map<String, Object> map : maps){

            Long id = ((Number) map.get("id")).longValue(); //❌ MySQL返回的id可能是Integer不是Long，直接强转会报错 Long id = (Long) map.get("id");
            String productName = map.get("product_name").toString();
            String description = map.get("description").toString();
            BigDecimal price1 = (BigDecimal) map.get("price");
            String category1 = map.get("category").toString();
            String imageUrl1 = map.get("image_url").toString();


            ProductSummaryDTO productSummaryDTO = new ProductSummaryDTO(
                    id,
                    productName,
                    description,
                    price1,
                    category1,
                    imageUrl1
            );
            productSummaryDTOS.add(productSummaryDTO);
        }
        if (productSummaryDTOS.isEmpty()) throw new AppException(404, "no itesm, failed to display");
        // 存入 Redis，1分钟过期（短一点，保证数据不会太旧）
        redis.opsForValue().set(key,toJson( productSummaryDTOS),10, TimeUnit.MINUTES);
        return productSummaryDTOS;
    }

    public boolean productAddSer(ProductAdminDTO productAdminDTO){
        boolean result = productRepo.productAddRepo(productAdminDTO);
        return result;
    }

    public boolean productRemoveSer(Long id){
        redis.delete("product:"+id);
        return productRepo.productRemoveRepo(id);
    }
    public boolean productUpdateSer(Long id,BigDecimal price){
        boolean result = productRepo.productUpdaterepo(id, price);
        redis.delete("product:" + id);
        return result;
    }
    public ProductSummaryDTO productSearchSer(Long id){
        String key = "product:"+id;
        String cached = redis.opsForValue().get(key);
        if(cached!=null){
            try {
            return parseJson(cached);
            }catch (Exception e){
                throw new RuntimeException("no search");
            }
        }

        ProductSummaryDTO productSummaryDTO = productRepo.productSearchrepo(id);

        // add into redis
        redis.opsForValue().set(key,toJson(productSummaryDTO),6,TimeUnit.HOURS);
        return productSummaryDTO;
    }

}
