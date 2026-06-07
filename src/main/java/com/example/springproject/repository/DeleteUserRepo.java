package com.example.springproject.repository;

import com.example.springproject.model.Users;
import com.example.springproject.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DeleteUserRepo {

    //security: authentication magager -->authorize password before delete the account
    //delete :  use jdbctemplate ->sql: delete user

    @Autowired
    public JdbcTemplate jdbcTemplate;


    public boolean deleteUser(Users user){
        String sql = "delete from users where user_Name=?";
        int update = jdbcTemplate.update(sql, user.getUserName());
        return update>0;
    }

    public String checkUserAndGetPassword(String username){
        String sql = "select password from users where user_Name=?";
        String hashpassword = jdbcTemplate.queryForObject(sql, String.class,username);
        return hashpassword;
    }

}
