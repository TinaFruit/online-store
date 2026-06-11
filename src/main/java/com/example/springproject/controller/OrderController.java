package com.example.springproject.controller;

import com.example.springproject.security.JwtUtil;
import com.example.springproject.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtUtil jwtUtil;

//    AuthenticationManager — 用来验证用户名密码，登录的时候用：
//    Authentication auth — 用来获取已登录用户的信息，登录之后的请求用：
    @PostMapping("/putOrder")
    public ResponseEntity<String> putOrder(@RequestBody List<HashMap<String, Integer>> productLists, Authentication auth){
        try {
            boolean b = orderService.putOrderServ(productLists, auth);
            if (b) return ResponseEntity.ok("下单成功");
            return ResponseEntity.status(500).body("下单失败");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // "库存不足" 等错误信息
        }

// 客户 只需要提供
// product_id      INT, ---- 要✅
// quantity        INT,   ---- 要✅
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

        //1.只有admin才能删订单
        if(role == null || !role.equals("admin")){//admin 删订单是不检查 user_id 的，任何订单都能删。
            return ResponseEntity.status(403).body("you are not admin");
        }

        //2.准备删订单
        boolean b = orderService.deleteOrderServ(id);
        if(b) return ResponseEntity.ok("deleted successfully");
        return ResponseEntity.status(500).body("failed deletion");

    }

}
