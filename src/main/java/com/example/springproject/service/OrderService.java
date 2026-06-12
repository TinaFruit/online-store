package com.example.springproject.service;

import com.example.springproject.model.OrderDetailDTO;
import com.example.springproject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    public JdbcTemplate jdbcTemplate;
    public boolean putOrderServ(List<HashMap<String, Integer>> products, Authentication auth) {
        // 从JWT取username → 查数据库拿user_id
        String username = auth.getName();
        Integer userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE user_Name=?", Integer.class, username
        );
        return orderRepository.putOrderRepo(products, userId);
    }
    public boolean deleteOrderServ(int orderId) {
       return orderRepository.deleteOrderRepo(orderId);
    }

    public boolean amendentServ(List<HashMap<String, Integer>> products, int orderId) {
        return orderRepository.amendentRepo(products,orderId);
    }

    public boolean returnProductsServ(int orderId) {
        return orderRepository.returnProductsRepo(orderId);
    }

    public boolean updateOrdersServ(int orderId, String newStatus) {
        return orderRepository.updateOrdersRepo(orderId, newStatus);
    }

    public  List<OrderDetailDTO>  searchOderServ(String username) {

        return orderRepository.searchOrderRepo(username);

    }
}
