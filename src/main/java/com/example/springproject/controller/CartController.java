package com.example.springproject.controller;

import com.example.springproject.model.CartItemsDTO;
import com.example.springproject.model.CartJoinProductDTO;
import com.example.springproject.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CartItemsDTO cartItemsDTO) {
        if (!cartService.add(cartItemsDTO)) return ResponseEntity.status(500).body("failed to add");
        return ResponseEntity.ok("success added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam int id) {
        if (!cartService.delete(id)) return ResponseEntity.status(500).body("failed to delete");
        return ResponseEntity.ok("success deleted");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestParam int quantity, @RequestParam int id) {
        if (!cartService.update(quantity, id)) return ResponseEntity.status(500).body("failed to update");
        return ResponseEntity.ok("success updated");
    }
    @GetMapping("/searchOne")
    public ResponseEntity<?> searchOne(@RequestParam int userid) {
        List<CartJoinProductDTO> result = cartService.searchOne(userid);
        if (result == null || result.isEmpty()) return ResponseEntity.status(404).body("没搜索到");
        return ResponseEntity.ok(result);
    }
    @GetMapping("/searchAll")
    public ResponseEntity<?> searchList(@RequestParam int userid) {
        List<CartJoinProductDTO> result = cartService.searchList(userid);
        if (result == null || result.isEmpty()) return ResponseEntity.status(404).body("没搜索到");
        return ResponseEntity.ok(result);
    }
}