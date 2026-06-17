package com.example.springproject.service;

import com.example.springproject.exeption.DuplicateRegisterException;
import com.example.springproject.model.Users;
import com.example.springproject.repository.RegisterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    @Autowired
    private RegisterRepo registerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registerServ(Users user){
        //check duplicate
        if(registerRepo.checkDuplicatedRegister(user)){
            throw new DuplicateRegisterException(409, "conflict, failed to register due to the username already exists"); // ResponseEntity.status(409).body("用户已存在 注册失败");//409 用户名已存在、数据重复

        }
        // add
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        return registerRepo.registerRepo(user);
    }

}
