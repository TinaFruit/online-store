package com.example.springproject.event;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class OrderCreatedEvent {

    private Integer userId;
    private List<HashMap<String, Integer>> products;
    private LocalDateTime createdAt;

    // Kafka JsonDeserializer
    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Integer userId, List<HashMap<String, Integer>> products, LocalDateTime createdAt) {
        this.userId = userId;
        this.products = products;
        this.createdAt = createdAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<HashMap<String, Integer>> getProducts() {
        return products;
    }

    public void setProducts(List<HashMap<String, Integer>> products) {
        this.products = products;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
