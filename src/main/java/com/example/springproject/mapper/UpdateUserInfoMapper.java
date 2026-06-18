package com.example.springproject.mapper;

import com.example.springproject.model.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateUserInfoMapper {
    int updateUserSql(Users user);
}