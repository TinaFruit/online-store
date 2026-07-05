package com.example.springproject.controller;

import com.example.springproject.model.Users;
import com.example.springproject.repository.RegisterRepo;
import com.example.springproject.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Register {

    @Autowired
    private RegisterService registerServ;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user){
      registerServ.registerServ(user);
       return ResponseEntity.ok("register successfully");
    }






}
