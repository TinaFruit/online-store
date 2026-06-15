package com.example.springproject.service;

import com.example.springproject.exeption.AppException;
import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import com.example.springproject.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductSer {
    @Autowired
    private ProductRepo productRepo;

    public List<ProductSummaryDTO> disaplaySer(int page, int size){

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
        return productSummaryDTOS;
    }


    public boolean productAddSer(ProductAdminDTO productAdminDTO){
        boolean result = productRepo.productAddRepo(productAdminDTO);
        return result;
    }

    public boolean productRemoveSer(Long id){
        return productRepo.productRemoveRepo(id);
    }
    public boolean productUpdateSer(Long id,BigDecimal price){
        return productRepo.productUpdaterepo(id,price);
    }
    public ProductSummaryDTO productSearchSer(Long id){
        return productRepo.productSearchrepo(id);
    }

}
