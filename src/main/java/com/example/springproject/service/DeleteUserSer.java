package com.example.springproject.service;

import com.example.springproject.model.Users;
import com.example.springproject.repository.CommonUtil;
import com.example.springproject.repository.DeleteUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserSer {

    @Autowired
    private DeleteUserRepo deleteUserRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommonUtil commonUtil;

    public boolean deleteUserSer(Users user){
        //check user 并提供数据库的密码
        String encodePassword = commonUtil.checkUserAndGetPassword(user.getUserName());
        if (encodePassword == null) return false;
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
