package com.example.springproject.service;

import com.example.springproject.exeption.AppException;
import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import com.example.springproject.repository.ProductRepo;
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
    public List<ProductSummaryDTO> displaySer(int page, int size) {
        //1.check redis
        String key ="product:"+page+":"+size;
        List<ProductSummaryDTO> cached = getAndDeserializeFromCache(key); //cache has data , doesn't have the data
        if(cached != null){
           return cached;
        }

        //2.check database
        List<ProductSummaryDTO> productSummaryDTOS = fetchAndMapFromDb(page, size);

        //3.saveToCache
        saveToCache(key,productSummaryDTOS);
        return productSummaryDTOS;
    }


    /// check redis
    public List<ProductSummaryDTO> getAndDeserializeFromCache (String key){
        String cached = redis.opsForValue().get(key);
        if(cached != null){
            try {
                return objectMapper.readValue(cached, objectMapper.getTypeFactory().constructCollectionType(List.class, ProductSummaryDTO.class));
            }catch (Exception e) {
                throw new RuntimeException("cached issue");
            }
        }
        return null;
    }

    /// fetchAndMapFromDb
    public List<ProductSummaryDTO> fetchAndMapFromDb(int page, int size){
        List<Map<String, Object>> maps = productRepo.displayRepo(page, size); //mock
        if (maps == null || maps.isEmpty()) throw new AppException(404, "no found itesms");

        List<ProductSummaryDTO> productSummaryDTOS = new ArrayList<>();
        for (Map<String, Object> map : maps) {

            Long id = ((Number) map.get("id")).longValue(); //❌ 要用(Number)转类型---MySQL返回的id可能是Integer不是Long，直接强转会报错 Long id = (Long) map.get("id");
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
        return productSummaryDTOS;
    }

    ///saveToCache
    public void saveToCache(String key, Object data){
        redis.opsForValue().set(key,toJson(data),10, TimeUnit.MINUTES);
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
        saveToCache(key,productSummaryDTO);
        return productSummaryDTO;
    }

}
