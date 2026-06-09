package com.example.springproject.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CartJoinProductDTO {
    private Long cartItemId;
//        @NotBlank(message="cannot be empty or null")// @NotBlank 只能用在 String 上
    // Long 类型要用 @NotNull
     @NotNull
        private Long productId;
        private String productName;
        private String imageUrl;
        @Size(max = 6, min = 2)
        private BigDecimal price;
        private Integer quantity;
        private Integer selected;
        private BigDecimal subtotal;  // price * quantity

        public CartJoinProductDTO() {}

        public CartJoinProductDTO(Long cartItemId, Long productId, String productName,
                                  String imageUrl, BigDecimal price,
                                  Integer quantity, Integer selected) {
            this.cartItemId  = cartItemId;
            this.productId   = productId;
            this.productName = productName;
            this.imageUrl    = imageUrl;
            this.price       = price;
            this.quantity    = quantity;
            this.selected    = selected;
            this.subtotal    = price.multiply(BigDecimal.valueOf(quantity));
        }


    public Long getCartItemId()       { return cartItemId; }
        public Long getProductId()        { return productId; }
        public String getProductName()    { return productName; }
        public String getImageUrl()       { return imageUrl; }
        public BigDecimal getPrice()      { return price; }
        public Integer getQuantity()      { return quantity; }
        public Integer getSelected()      { return selected; }
        public BigDecimal getSubtotal()   { return subtotal; }

        public void setCartItemId(Long cartItemId)       { this.cartItemId = cartItemId; }
        public void setProductId(Long productId)         { this.productId = productId; }
        public void setProductName(String productName)   { this.productName = productName; }
        public void setImageUrl(String imageUrl)         { this.imageUrl = imageUrl; }
        public void setPrice(BigDecimal price)           { this.price = price; }
        public void setQuantity(Integer quantity)        { this.quantity = quantity; }
        public void setSelected(Integer selected)        { this.selected = selected; }
        public void setSubtotal(BigDecimal subtotal)     { this.subtotal = subtotal; }
    }
