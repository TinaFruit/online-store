package com.example.springproject.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDetailDTO {
    private Integer orderId;
    private Integer userId;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime orderCreatedAt;

    private Integer productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private Integer quantity;
    private LocalDateTime detailCreatedAt;


    // getters and setters

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderCreatedAt(LocalDateTime orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setDetailCreatedAt(LocalDateTime detailCreatedAt) {
        this.detailCreatedAt = detailCreatedAt;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getOrderCreatedAt() {
        return orderCreatedAt;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getDetailCreatedAt() {
        return detailCreatedAt;
    }
}