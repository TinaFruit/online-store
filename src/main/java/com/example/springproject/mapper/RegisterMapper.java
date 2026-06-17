package com.example.springproject.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface RegisterMapper {

    int add(@Param("username") String username, @Param("password") String password);
    Integer checkDuplicatedRegister(@Param("username") String username);


}
