package com.example.springproject.service;

import com.example.springproject.model.Users;
import com.example.springproject.repository.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginSevice {

    @Autowired
    private LoginRepo loginRepo;
    //去repository找方法去数据库
//    public String checkLogin(Users user){
//
//        boolean result = loginRepo.checkLoginrepo(user);
//        if(result){
//            return "登陆成功";
//        }else{
//            return "登陆失败";
//        }
//    }

//    public String register(Users user){
//
//        boolean result = loginRepo.register(user);
//        if(result){
//            return "注册成功";
//        }else{
//            return "注册失败";
//        }
//    }

}
