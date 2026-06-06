package com.example.springproject.controller;

import com.example.springproject.model.Users;
import com.example.springproject.service.LoginSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class register {
    @Autowired
    private LoginSevice loginSevice;

    @PostMapping("/register")
    public String register(@RequestBody Users user){
        //用户提供 username password,... userdetails
        //数据库存入，密码要用Springsecurity自动调用的
       return ;
    }

}
