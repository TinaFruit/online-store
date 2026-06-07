package com.example.springproject.controller;


import com.example.springproject.model.Users;
import com.example.springproject.service.DeleteUserSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteUser {

    @Autowired
    public AuthenticationManager authenticationManager;
    @Autowired
    public DeleteUserSer deleteUserSer;

    @PostMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody Users use) {


           if(deleteUserSer.deleteUserSer(use)){
               return ResponseEntity.ok("delete成功");
           }
           return ResponseEntity.status(500).body("操作失败 or 密码错误");
    }
}
