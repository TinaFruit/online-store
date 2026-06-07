package com.example.springproject.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommonUtil {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    public String checkUserAndGetPassword(String username){
        String sql = "select password from users where user_Name=?";
        try {
            String hashpassword = jdbcTemplate.queryForObject(sql, String.class, username);
            return hashpassword;
        }catch (Exception e){
            return null;
        }

    }
}
