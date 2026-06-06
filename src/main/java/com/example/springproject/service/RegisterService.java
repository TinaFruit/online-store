package com.example.springproject.service;

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
            return false;
        }
        // add
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        return registerRepo.registerRepo(user);
    }

}
