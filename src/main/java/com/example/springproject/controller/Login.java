package com.example.springproject.controller;

import com.example.springproject.model.LoginResponse;
import com.example.springproject.model.Users;
import com.example.springproject.security.JwtUtil;
import com.example.springproject.security.UserDetailsServiceImpl;
import com.example.springproject.service.LoginSevice;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Login {
    @Autowired
    private LoginSevice loginSevice;

    // 旧的 没有加入jwt 安全的 login
//    @PostMapping("/login")
//    public String loginUser(@RequestBody Users user){
//        //调用service方法去验证
//        return loginSevice.checkLogin(user);
//    }

//新的 加入jwt 安全的 login。开始--验证是否密码正确-->登陆后-->拿到生出的token-->成功

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody Users user){
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUserName(),
                            user.getPassword()));

            String role = authenticate.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");; //checking
            String token = jwtUtil.generateToken(authenticate.getName(),role);//  // 生成 Token
            return ResponseEntity.ok(new LoginResponse(token,authenticate.getName()));
        }catch (Exception e){
            return ResponseEntity.status(401).body("登陆失败，用户名不对 或者 token 不对");
        }
    }
}
