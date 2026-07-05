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

//    AuthenticationManager — 用来验证用户名密码，登录的时候用：
//    Authentication auth — used for retrieving userDetails, after loging in. get the  userDetails
    @PostMapping("/putOrder")
    public ResponseEntity<String> putOrder(@RequestBody List<HashMap<String, Integer>> productLists, Authentication auth){
            boolean b = orderService.putOrderServ(productLists, auth);
            return ResponseEntity.ok("ordered successfully");

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> putOrder(@RequestHeader("Authorization") String token, @PathVariable int id)
//   方法2: public ResponseEntity<String> deleteOrder(Authentication authentication, @PathVariable int id)

    //@RequestHeader("Authorization") + jwtUtil.getRole(token)
    //→ 手动解析token取role
    //
    //Authentication authentication
    //→ JwtFilter已经帮你解析好了，直接取
    {
        //登录：
        //username + password
        //→ Spring Security 验证
        //→ 查数据库取 role
        //→ JWT 存入 username + role
        //→ 返回 token 给客户端
        //
        //删订单：
        //客户端带着 token
        //→ 从 token 取出 role
        //→ role == admin → 允许删除
        //→ role == user  → 403 拒绝

        String substring = token.substring(7);
        String role = jwtUtil.getRole(substring);

        //1.Only admin can delete any orders
        if(role == null || !role.equals("admin")){
            return ResponseEntity.status(403).body("you are not admin");
        }

        //2.prepare deleting orders
        boolean b = orderService.deleteOrderServ(id);
        if(b) return ResponseEntity.ok("deleted successfully");
        return ResponseEntity.status(500).body("failed deletion");

    }

    @PutMapping("/admendent/{orderId}")
    public ResponseEntity<String>  amendent(@RequestBody List<HashMap<String, Integer>> productLists, @PathVariable int orderId){
        boolean b = orderService.amendentServ(productLists,orderId);
        if(b) return ResponseEntity.ok("success added ");
        return ResponseEntity.status(500).body("failed to adment");
    }


    @PostMapping("return")
    public void returnProductsRepo(@RequestHeader("Authentication") String tokenwithprefix) {
        String token = tokenwithprefix.substring(7);
        String role = jwtUtil.getRole(token);
        //1.only admin can delete orders
        if(role == null || !role.equals("admin")){

        }
    }
    @PostMapping("/return/{orderId}")
    public ResponseEntity<String> returnProductsRepso(
            @PathVariable("orderId") int orderId,
            Authentication authentication) {

        // authentication verification
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

    @PutMapping("/status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable("orderId") int orderId,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        boolean result = orderService.updateOrdersServ(orderId, newStatus);

        if (result) {
            return ResponseEntity.ok("Order status updated to " + newStatus);
        }
        return ResponseEntity.status(500).body("Failed to update order status");
    }


    @GetMapping("/searchOrder")
    public ResponseEntity<?> searchOderByusername(Authentication auth){
        String name = auth.getName();
        List<OrderDetailDTO> orderDetailDTOS = orderService.searchOderServ(name);
        if (orderDetailDTOS.isEmpty()){
            return ResponseEntity.status(409).body("empty"); }
        return ResponseEntity.ok(  orderDetailDTOS) ;

    }
}
