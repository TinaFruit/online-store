package com.example.springproject.controller;

import com.example.springproject.model.OrderDetailDTO;
import com.example.springproject.security.JwtUtil;
import com.example.springproject.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/putOrder")
    public ResponseEntity<String> putOrder(@RequestBody List<HashMap<String, Integer>> productLists, Authentication auth) {
        boolean b = orderService.putOrderServ(productLists, auth);
        return ResponseEntity.ok("ordered successfully");
    }

    @PutMapping("/admendent/{orderId}")
    public ResponseEntity<String> amendent(@RequestBody List<HashMap<String, Integer>> productLists,
                                           @PathVariable int orderId,
                                           Authentication auth) {
        boolean b = orderService.amendentServ(productLists, orderId, auth);
        if (b) return ResponseEntity.ok("success added ");
        return ResponseEntity.status(500).body("failed to adment");
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable("orderId") int orderId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.status(403).body("Only admin can update order status");
        }

        String newStatus = body.get("status");
        boolean result = orderService.updateOrdersServ(orderId, newStatus);
        if (result) {
            return ResponseEntity.ok("Order status updated to " + newStatus);
        }
        return ResponseEntity.status(500).body("Failed to update order status");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@RequestHeader("Authorization") String token, @PathVariable int id) {
        String substring = token.substring(7);
        String role = jwtUtil.getRole(substring);

        if (role == null || !role.equalsIgnoreCase("admin")) {
            return ResponseEntity.status(403).body("you are not admin");
        }

        boolean b = orderService.deleteOrderServ(id);
        if (b) return ResponseEntity.ok("deleted successfully");
        return ResponseEntity.status(500).body("failed deletion");
    }

    @PostMapping("/return/{orderId}")
    public ResponseEntity<String> returnProductsRepso(
            @PathVariable("orderId") int orderId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Only admin can process returns");
        }

        boolean result = orderService.returnProductsServ(orderId);
        if (result) {
            return ResponseEntity.ok("Order returned successfully");
        }
        return ResponseEntity.status(500).body("Failed to process return");
    }

    @GetMapping("/searchOrder")
    public ResponseEntity<?> searchOderByusername(Authentication auth) {
        String name = auth.getName();
        List<OrderDetailDTO> orderDetailDTOS = orderService.searchOderServ(name);
        if (orderDetailDTOS.isEmpty()) {
            return ResponseEntity.status(409).body("empty");
        }
        return ResponseEntity.ok(orderDetailDTOS);
    }
}