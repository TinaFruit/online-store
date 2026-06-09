package com.example.springproject.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductAdminDTO {

    private Long id;
    @NotBlank(message="must to offer productName")
    private String productName;
    private String description;
    private BigDecimal price;        // 价格（必须！用BigDecimal避免精度问题）
    private Integer stockQuantity;
    private String category;         // 商品分类（电子/服装/食品等）
    private String imageUrl;         // 商品图片
    private String status;           // ACTIVE / INACTIVE / OUT_OF_STOCK
    @DateTimeFormat// ❌ 不需要// @DateTimeFormat 是用在 Controller 接收前端传来的日期字符串 DTO里的 createdAt/updatedAt 是数据库自动生成的，用户不会传这个
    private LocalDateTime createdAt;
    @DateTimeFormat// ❌ 不需要// @DateTimeFormat 是用在 Controller 接收前端传来的日期字符串 DTO里的 createdAt/updatedAt 是数据库自动生成的，用户不会传这个
    private LocalDateTime updatedAt;
    private Long sellerId;           // 卖家ID（Mini Amazon有第三方卖家）

    public ProductAdminDTO() {
    }

    public ProductAdminDTO(Long id, String productName, String description, BigDecimal price, Integer stockQuantity, String category, String imageUrl, String status, LocalDateTime createdAt, LocalDateTime updatedAt, Long sellerId) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sellerId = sellerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getSellerId() {
        return sellerId;
    }
}
