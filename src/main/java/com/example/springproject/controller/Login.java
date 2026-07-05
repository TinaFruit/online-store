package com.example.springproject.controller;

import com.example.springproject.exeption.AppException;
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

import java.util.HashMap;

@RestController
public class Login {
    @Autowired
    private LoginSevice loginSevice;

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
            String token = jwtUtil.generateToken(authenticate.getName(),role);// generate Token
            return ResponseEntity.ok(new LoginResponse(token,authenticate.getName()));
        }catch (Exception e){
            throw new AppException(401,"failed to login");
        }

    }
}
