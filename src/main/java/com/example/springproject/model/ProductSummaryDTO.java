package com.example.springproject.model;

import java.math.BigDecimal;

public class ProductSummaryDTO {
    private Long id;
    private String productName;
    private String description;
    private BigDecimal price;        // 价格（必须！用BigDecimal避免精度问题）
    private String category;         // 商品分类（电子/服装/食品等）
    private String imageUrl;         // 商品图片

    public ProductSummaryDTO() {
    }

    public ProductSummaryDTO(Long id, String productName, String description, BigDecimal price, String category, String imageUrl) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
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

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
