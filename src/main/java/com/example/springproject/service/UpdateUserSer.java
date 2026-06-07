package com.example.springproject.service;

import com.example.springproject.model.Users;
import com.example.springproject.repository.CommonUtil;
import com.example.springproject.repository.UpdateUserRepo;
import com.example.springproject.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserSer {
    @Autowired
    private UpdateUserRepo updateUserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommonUtil commonUtil;

    public boolean updateUserNameSer(Users user){
        String hashpassword = commonUtil.checkUserAndGetPassword(user.getUserName());
        if (hashpassword == null) return false;
        boolean matches = passwordEncoder.matches(user.getPassword(), hashpassword);
        if(!matches){
            return false;
        }
        //update username
        return updateUserRepo.updateUserNamerep(user);
    }

    public boolean updateUserPasswordOrEmailServ(Users user){
        String hashpassword = commonUtil.checkUserAndGetPassword(user.getUserName());
        if (hashpassword == null) return false;
        boolean matches = passwordEncoder.matches(user.getPassword(), hashpassword);
        if(!matches){
            return false;
        }
        //encode new password
        String encode = passwordEncoder.encode(user.getUpdatedPassword());
        user.setUpdatedPassword(encode);
        //update password + email
        return updateUserRepo.updateUserPasswordOrEmailRepo(user);
    }

}
