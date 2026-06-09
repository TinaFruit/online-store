package com.example.springproject.controller;

import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import com.example.springproject.service.ProductSer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/product")
public class ProductsFuction {

    //display getmapping 商品展示  // 数据库： 分页 + 索引优化
    //remove get 商品下架  // 数据库： 软删除 / 上下架状态
    //search post 搜索商品  // 数据库： like / 分词 / 索引 商品名字/id
    //add post 添加商品 // 数据库：管理员权限
    //update put 更新商品   // 数据库：DTO + 更新流程

    @Autowired
    private ProductSer ProductSer;

    @GetMapping("/display")
    public ResponseEntity<?> productDisaplay(@RequestParam(defaultValue = "1") int page,  @RequestParam(defaultValue = "10")  int size){
        page =page-1;
        List<ProductSummaryDTO> maps = ProductSer.disaplaySer(page, size);
        if (maps == null) return ResponseEntity.status(500).body("failed to display");
        return ResponseEntity.ok(maps);
    }

    //crud:
    @PostMapping("/add")
    public ResponseEntity<String> productAdd(@Valid @RequestBody ProductAdminDTO productAdminDTO){
       if(ProductSer.productAddSer(productAdminDTO)){
           return ResponseEntity.ok("add successfully");
       }
        return ResponseEntity.status(500).body("failed to Add");
    }

    @GetMapping("/remove")
    public ResponseEntity<String> productRemove(@RequestParam Long id){
        boolean b = ProductSer.productRemoveSer(id);
        if(b){
           return ResponseEntity.ok("removed successfully");
        }
        return  ResponseEntity.status(500).body("failed to remove");
    }
    @PutMapping("/update")
    public ResponseEntity<String> productUpdate(@RequestParam Long id, BigDecimal price){
        boolean b = ProductSer.productUpdateSer(id,price);
        if(b){
           return ResponseEntity.ok("updated successfully");

        }
        return  ResponseEntity.status(500).body("failed to update");
    }

    @GetMapping("/search")
    public ResponseEntity<?> productSearch(@RequestParam Long id){
        System.out.println("查询id: " + id);
        ProductSummaryDTO productSummaryDTO = ProductSer.productSearchSer(id);
        if(productSummaryDTO == null){
            ResponseEntity.status(500).body("failed to search");

        }
        return   ResponseEntity.ok(productSummaryDTO);
    }




}
