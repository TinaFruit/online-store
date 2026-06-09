package com.example.springproject.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class CartItemsDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer selected;
    @DateTimeFormat// ❌ 不需要// @DateTimeFormat 是用在 Controller 接收前端传来的日期字符串 DTO里的 createdAt/updatedAt 是数据库自动生成的，用户不会传这个
    private LocalDateTime createdAt;
    @DateTimeFormat// ❌ 不需要// @DateTimeFormat 是用在 Controller 接收前端传来的日期字符串 DTO里的 createdAt/updatedAt 是数据库自动生成的，用户不会传这个

    private LocalDateTime updatedAt;

    public CartItemsDTO() {
    }

    public CartItemsDTO(Long id, Long userId, Long productId,
                        Integer quantity, Integer selected,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.selected = selected;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getSelected() {
        return selected;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

