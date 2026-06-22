package com.example.springproject.service;

import com.example.springproject.model.CartItemsDTO;
import com.example.springproject.model.CartJoinProductDTO;
import com.example.springproject.repository.CartRepository;

import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public boolean add(CartItemsDTO cartItemsDTO){
        return cartRepository.add( cartItemsDTO);
    }
    public boolean delete(int id){
       return cartRepository.delete(id);
    }
    public boolean update(int quantity, int id){
        return cartRepository.updateQuantity( quantity,  id);
    }
    public List<CartJoinProductDTO> searchOne(int userid) {

        List<Map<String, Object>> maps = cartRepository.searchList(userid);

        List<CartJoinProductDTO> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            CartJoinProductDTO dto = new CartJoinProductDTO(
                    ((Number) map.get("id")).longValue(),
                    ((Number) map.get("product_id")).longValue(),
                    map.get("product_name").toString(),
                    map.get("image_url").toString(),
                    (BigDecimal) map.get("price"),
                    ((Number) map.get("quantity")).intValue(),
                    ((Number) map.get("selected")).intValue()
            );
            list.add(dto);
        }
        return list;
    }
    public List<CartJoinProductDTO> searchList(int userid) {

        List<Map<String, Object>> maps = cartRepository.searchList(userid);

        List<CartJoinProductDTO> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            CartJoinProductDTO dto = new CartJoinProductDTO(
                    ((Number) map.get("id")).longValue(),
                    ((Number) map.get("product_id")).longValue(),
                    map.get("product_name").toString(),
                    map.get("image_url").toString(),
                    (BigDecimal) map.get("price"),
                    ((Number) map.get("quantity")).intValue(),
                    ((Number) map.get("selected")).intValue()
            );
            list.add(dto);
        }
        return list;
    }
}
