package com.example.springproject.repository;

import com.example.springproject.exeption.AppException;
import com.example.springproject.mapper.UserMapper;
import com.example.springproject.model.Users;
import com.example.springproject.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DeleteUserRepo {

    //security: authentication magager -->authorize password before delete the account
    //delete :  use jdbctemplate ->sql: delete user

//    @Autowired
//    public JdbcTemplate jdbcTemplate;

    @Autowired
    public UserMapper userMapper;

    public boolean deleteUser(Users user){
        int i = userMapper.deleteUser(user.getUserName());
        if (i <=0) throw new AppException(404,"not found,delete failed");
        return true;
    }

}
