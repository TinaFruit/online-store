package com.example.springproject.repository;

import com.example.springproject.mapper.CartMapper;
import com.example.springproject.model.CartItemsDTO;
import com.example.springproject.model.CartJoinProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class CartRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CartMapper cartMapper;
    public boolean add(CartItemsDTO cartItemsDTO){
        //before add,check
//        String check = "SELECT COUNT(*) FROM cart_items WHERE user_id = ? AND product_id = ?";
//        int count = jdbcTemplate.queryForObject(check, Integer.class,
//                cartItemsDTO.getUserId(), cartItemsDTO.getProductId());
        int count =  cartMapper.countCartItem(Math.toIntExact(cartItemsDTO.getUserId()), Math.toIntExact(cartItemsDTO.getProductId()));

        if (count > 0) {
            // 已存在 → 累加数量
            String update = "UPDATE cart_items SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
            return jdbcTemplate.update(update, cartItemsDTO.getQuantity(),
                    cartItemsDTO.getUserId(), cartItemsDTO.getProductId()) > 0;
        }
            // created_at 和 updated_at 在表里已经设了 DEFAULT NOW()
            String sql = "INSERT INTO cart_items(user_id, product_id, quantity) VALUES (?, ?, ?)";

            int update = jdbcTemplate.update(sql,
                    cartItemsDTO.getUserId(),
                    cartItemsDTO.getProductId(),
                    cartItemsDTO.getQuantity()
            );
            return update > 0;

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
        //和之前的product 一样的逻辑
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
        //和之前的product 一样的逻辑
//        String sql = "select * from cart_items c join products p on c.product_id=p.id where c.user_id = ?";
//         List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql,userid);

         return  cartMapper.maps(userid);
    }
}
