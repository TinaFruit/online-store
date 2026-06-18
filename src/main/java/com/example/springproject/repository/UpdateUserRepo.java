package com.example.springproject.repository;


import com.example.springproject.mapper.UpdateUserInfoMapper;
import com.example.springproject.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UpdateUserRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UpdateUserInfoMapper updateuserMapper;

    public boolean updateUserPasswordOrEmailRepo(Users user){
//        if(user.getEmail() == null){
            int update = updateuserMapper.updateUserSql(user);
            return update>0;
//            String sqlNoEmail="update users set password= ? where user_Name = ?";
//            int update = jdbcTemplate.update(sqlNoEmail, user.getUpdatedPassword(), user.getUserName());

//        }
//        String sqlHasEmail="update users set password= ?, email=? where user_Name = ?";
//        int update = jdbcTemplate.update(sqlHasEmail, user.getUpdatedPassword(), user.getUpdatedEmail(), user.getUserName());
//        return update>0;
    }

    public boolean updateUserNamerep(Users user){
        int update = updateuserMapper.updateUserSql(user);
        return update>0;
//        String sqlHasUsername="update users set user_Name = ? where user_Name=?";
//        int update = jdbcTemplate.update(sqlHasUsername,user.getUpdatedUserName(),user.getUserName());
//        return update>0;
    }

}


