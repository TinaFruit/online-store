package com.example.springproject.repository;

import com.example.springproject.model.Users;
//import com.example.springproject.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoginRepo {
    //go to database to check the user info
    //1.通过yaml连接数据库
    //2.写sql 查询数据库的users表格有没有对应的数据

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //这里不需要    private PasswordUtil passwordUtlity; 因为 PasswordUtil是static 不需要对象
//    private PasswordUtil passwordUtlity;

//    public boolean checkLoginrepo(Users user){
//        String userName = user.getUserName();
//        String password = user.getPassword();
//
////        String sql = "select count(*) from Users where user_name = ? and password = ?";
////        Integer count = jdbcTemplate.queryForObject(
////                sql,
////                Integer.class,
////                userName,
////                password
////        );
//
//        //get users, then check password 从数据库查出这个用户，然后拿“数据库密码”和“你输入的密码”做 BCrypt 比较
//        String sql = "select * from Users where user_name = ?";
//        Users dbUser = jdbcTemplate.queryForObject(
//                sql,
//                new Object[]{userName},
//                (rs, rowNum) -> {
//                    Users u = new Users();
//                    u.setUserName(rs.getString("user_name"));
//                    u.setPassword(rs.getString("password"));
//                    return u;
//                }
//                // queryForObject() 用于查询单条记录，
//                // RowMapper 用于把数据库返回的 ResultSet 映射（Mapping）成 Java 对象，例如把 Users 表的一行数据转换成一个 Users 实例。
//        );
//
//        // ✔ 正确：raw vs db hash
//        return PasswordUtil.matches(password, dbUser.getPassword());
//    }

//    public boolean register(Users user){
//        String sql ="INSERT INTO users (user_name, password, email) VALUES (?, ?, ?)";
//        int result = jdbcTemplate.update(sql, user.getUserName(), PasswordUtil.encode(user.getPassword()), user.getEmail());
//        return result>0;
//    }
}
