package com.example.springproject.controller;

import com.example.springproject.model.ProductAdminDTO;
import com.example.springproject.model.ProductSummaryDTO;
import com.example.springproject.service.ProductSer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductsFuction {

    @Autowired
    private ProductSer ProductSer;

    @GetMapping("/display")
    public ResponseEntity<?> productdisplay(@RequestParam(defaultValue = "1") int page,  @RequestParam(defaultValue = "10")  int size){
        page =page-1;
        List<ProductSummaryDTO> maps = ProductSer.displaySer(page, size);
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
