package com.example.springproject.controller;

import com.example.springproject.model.CartItemsDTO;
import com.example.springproject.model.CartJoinProductDTO;
import com.example.springproject.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /***
     * CartController is used for adding ,delete , upadte , search one or more items (item,quality)
     * */
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CartItemsDTO cartItemsDTO, Authentication auth) {
        String username =  auth.getName();
        if (!cartService.add(cartItemsDTO, username)) return ResponseEntity.status(500).body("failed to add");
        return ResponseEntity.ok("success added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam int id, Authentication auth) {
        if (!cartService.delete(id, auth.getName())) return ResponseEntity.status(500).body("failed to delete");
        return ResponseEntity.ok("success deleted");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestParam int quantity, @RequestParam int id, Authentication auth) {
        if (!cartService.update(quantity, id, auth.getName())) return ResponseEntity.status(500).body("failed to update");
        return ResponseEntity.ok("success updated");
    }

    @GetMapping("/searchAll")
    public ResponseEntity<?> searchList(Authentication auth) {
        String username =  auth.getName();
        List<CartJoinProductDTO> result = cartService.searchList(username);
        if (result == null || result.isEmpty()) return ResponseEntity.status(404).body("no found");
        return ResponseEntity.ok(result);
    }
}