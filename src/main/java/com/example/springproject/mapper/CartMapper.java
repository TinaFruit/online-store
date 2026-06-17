package com.example.springproject.mapper;

import com.example.springproject.model.CartJoinProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CartMapper {
    CartJoinProductDTO searchCart(@Param("id") int id);
    List<Map<String, Object>> maps(@Param("userid") int userid);
}
