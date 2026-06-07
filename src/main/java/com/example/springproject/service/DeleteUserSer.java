package com.example.springproject.service;

import com.example.springproject.controller.DeleteUser;
import com.example.springproject.model.Users;
import com.example.springproject.repository.DeleteUserRepo;
import com.example.springproject.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserSer {

    @Autowired
    private DeleteUserRepo deleteUserRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean deleteUserSer(Users user){
        //check user 并提供数据库的密码
        String encodePassword = deleteUserRepo.checkUserAndGetPassword(user.getUserName());
        //match密码是否相同
        boolean matches = passwordEncoder.matches(user.getPassword(), encodePassword);
        if(!matches){
            return false;
        }
        boolean result =
                deleteUserRepo.deleteUser(user);
        return result;
    }
}
