package com.example.springproject.repository;

import com.example.springproject.mapper.CartMapper;
import com.example.springproject.model.CartItemsDTO;
import com.example.springproject.model.CartJoinProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;
import java.util.List;
import java.util.Map;

@Repository
public class CartRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CartMapper cartMapper;
    public boolean add(CartItemsDTO cartItemsDTO){

        int count =  cartMapper.countCartItem(Math.toIntExact(cartItemsDTO.getUserId()), Math.toIntExact(cartItemsDTO.getProductId()));

        if (count > 0) {
            String update = "UPDATE cart_items SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
            return jdbcTemplate.update(update, cartItemsDTO.getQuantity(),
                    cartItemsDTO.getUserId(), cartItemsDTO.getProductId()) > 0;
        }
            String sql = "INSERT INTO cart_items(user_id, product_id, quantity) VALUES (?, ?, ?)";

            int update = jdbcTemplate.update(sql,
                    cartItemsDTO.getUserId(),
                    cartItemsDTO.getProductId(),
                    cartItemsDTO.getQuantity()
            );
            return update > 0;

    }

    public Integer getUserIdByCartItemId(int id) {
        String sql = "select user_id from cart_items where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public boolean delete(int id){
        String sql = "delete from cart_items where id = ?";
        int update = jdbcTemplate.update(sql, id);
        return update>0;
    }
    public boolean updateQuantity(int quantity, int id){
        String sql = "update cart_items set quantity = ? where id = ? ";
        int update = jdbcTemplate.update(sql, quantity, id);
        return update>0;
    }
    public CartJoinProductDTO search(int id){

//        String sql = "select * from cart_items c join products p on c.product_id=p.id where c.id = ?";
//        CartJoinProductDTO cartJoinProductDTO = jdbcTemplate.queryForObject(sql, (rs, row) -> new CartJoinProductDTO(
//                rs.getLong("id"),
//                rs.getLong("product_id"),
//                rs.getString("product_name"),
//                rs.getString("image_url"),
//                rs.getBigDecimal("price"),
//                rs.getInt("quantity"),
//                rs.getInt("selected")
//        ), id);
        return  cartMapper.searchCart(id);
    }

    public  List<Map<String, Object>> searchList(int userid){

//        String sql = "select * from cart_items c join products p on c.product_id=p.id where c.user_id = ?";
//         List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql,userid);

         return  cartMapper.maps(userid);
    }
}
