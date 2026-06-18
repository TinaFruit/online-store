package com.example.springproject.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int deleteUser(@Param("username") String username);
    String checkPassword(@Param("username") String username);

}
