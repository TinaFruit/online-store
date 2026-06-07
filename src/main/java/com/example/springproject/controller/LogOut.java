package com.example.springproject.controller;

import com.example.springproject.service.LogoutSer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogOut {
    //remove cookie:make jwt token invalid

    @Autowired
    private LogoutSer logoutSer;

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest){
        String header = httpServletRequest.getHeader("Authorization");
        String token = header.substring(7);
       if(logoutSer.logoutSer(token)){
           return ResponseEntity.ok("logout successfully");
       }
       return ResponseEntity.status(401).body("failed logout");

    }

}
