package com.example.springproject.service;

import com.example.springproject.event.OrderCreatedEvent;
import com.example.springproject.event.OrderEventProducer;
import com.example.springproject.model.OrderDetailDTO;
import com.example.springproject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private OrderEventProducer orderEventProducer;

    public boolean putOrderServ(List<HashMap<String, Integer>> products, Authentication auth) {
        String username = auth.getName();
        Integer userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE user_Name=?", Integer.class, username
        );
        boolean success = orderRepository.putOrderRepo(products, userId);

        if (success) {
            OrderCreatedEvent event = new OrderCreatedEvent(userId, products, LocalDateTime.now());
            orderEventProducer.sendOrderCreatedEvent(event);
        }

        return success;
    }

    public boolean deleteOrderServ(int orderId) {
       return orderRepository.deleteOrderRepo(orderId);
    }

    public boolean amendentServ(List<HashMap<String, Integer>> products, int orderId, Authentication auth) {
        String username = auth.getName();
        Integer currentUserId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE user_Name=?", Integer.class, username
        );
        Integer ownerId = orderRepository.getOrderOwnerId(orderId);

        if (ownerId == null) {
            throw new RuntimeException("order not found");
        }
        if (!ownerId.equals(currentUserId)) {
            throw new RuntimeException("you are not allowed to amend this order");
        }
        return orderRepository.amendentRepo(products, orderId);
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
