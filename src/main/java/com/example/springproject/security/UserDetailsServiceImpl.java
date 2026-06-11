package com.example.springproject.security;

import com.example.springproject.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 直接复用你原来的 SQL 逻辑
        String sql = "select * from Users where user_name = ?";

        Users dbUser = null;
        try {
            dbUser = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{username},
                    (rs, rowNum) -> {
                        Users u = new Users();
                        u.setUserName(rs.getString("user_name"));
                        u.setPassword(rs.getString("password"));
                        u.setRole(rs.getString("role"));
                        return u;
                    }
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 告诉 Spring Security：这个用户的用户名、密码、角色是什么
        return User.withUsername(dbUser.getUserName())
                .password(dbUser.getPassword())
                .roles(dbUser.getRole()) // 改这里，从数据库取
                .build();
    }




}