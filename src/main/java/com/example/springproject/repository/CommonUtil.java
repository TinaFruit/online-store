package com.example.springproject.repository;

import com.example.springproject.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public class CommonUtil {
//    @Autowired
//    public JdbcTemplate jdbcTemplate;
    @Autowired
    public UserMapper userMapper;

    public String checkUserAndGetPassword(String username){
            return userMapper.checkPassword(username);
//        String sql = "select password from users where user_Name=?";
//        try {
//            String hashpassword = jdbcTemplate.queryForObject(sql, String.class, username);
//            return hashpassword;
//        }catch (Exception e){
//            return null;
//        }

    }
}
