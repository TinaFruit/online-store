package com.example.springproject.service;

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

    public void amendentServ() {
    }

    public void preturnProductsServ() {
    }

    public void updateOrdersServ() {
    }

    public void searchOderServ() {

    }
}
