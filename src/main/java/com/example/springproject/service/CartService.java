package com.example.springproject.service;

import com.example.springproject.exeption.AppException;
import com.example.springproject.exeption.NoAnydisplayException;
import com.example.springproject.mapper.UserMapper;
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
    @Autowired
    private UserMapper userMapper;

    public boolean add(CartItemsDTO cartItemsDTO, String username){

        Long userId = ((Number)userMapper.checkUserId(username)).longValue();
        cartItemsDTO.setUserId(userId);
        return cartRepository.add( cartItemsDTO);
    }

    public boolean delete(int id, String username) {
        Long currentUserId = ((Number) userMapper.checkUserId(username)).longValue();
        Integer ownerId = cartRepository.getUserIdByCartItemId(id);

        if (ownerId == null) {
            throw new NoAnydisplayException(404, "cart item not found");
        }
        if (!ownerId.equals(currentUserId.intValue())) {
            throw new AppException(403, "you are not allowed to delete this cart item");
        }
        return cartRepository.delete(id);
    }

    public boolean update(int quantity, int id, String username) {
        Long currentUserId = ((Number) userMapper.checkUserId(username)).longValue();
        Integer ownerId = cartRepository.getUserIdByCartItemId(id);

        if (ownerId == null) {
            throw new NoAnydisplayException(404, "cart item not found");
        }
        if (!ownerId.equals(currentUserId.intValue())) {
            throw new AppException(403, "you are not allowed to update this cart item");
        }
        return cartRepository.updateQuantity(quantity, id);
    }
    public List<CartJoinProductDTO> searchList(String username) {
        int userId = userMapper.checkUserId(username);
        List<Map<String, Object>> maps = cartRepository.searchList(userId);

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
