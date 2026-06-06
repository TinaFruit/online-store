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
        //用户提供 username password,... userdetails
        //数据库存入，密码要用Springsecurity自动调用的加密存入 -- 写在
        //不能提供token 因为他只是注册
        boolean b = registerServ.registerServ(user);
        if(b){return ResponseEntity.ok("注册成功");}
        return ResponseEntity.status(409).body("用户已存在 注册失败");//409 用户名已存在、数据重复
    }

}
