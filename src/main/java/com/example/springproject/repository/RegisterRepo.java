package com.example.springproject.repository;

import com.example.springproject.exeption.UpdatedFailed;
import com.example.springproject.mapper.RegisterMapper;
import com.example.springproject.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class RegisterRepo {
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RegisterMapper registerMapper;

    public boolean registerRepo(Users user){
//        String sql = "insert into Users (user_name, password) values(?,?)";
//        int update = jdbcTemplate.update(sql,user.getUserName(),user.getPassword());
        int update = registerMapper.add(user.getUserName(),user.getPassword());
        if(update<=0) throw new UpdatedFailed(403, "forbidden, failed to register");

        return update>0;
    }

    public boolean checkDuplicatedRegister(Users user){
//        String sql = "select count(*) from Users where user_name = ?";
//        Integer result = jdbcTemplate.queryForObject(sql,Integer.class,user.getUserName());
        Integer  result = registerMapper.checkDuplicatedRegister(user.getUserName());
        return result != null && result>0;
    }


}
