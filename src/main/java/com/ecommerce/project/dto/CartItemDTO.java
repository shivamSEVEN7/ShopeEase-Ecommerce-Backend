package com.ecommerce.project.dto;

import com.ecommerce.project.model.Product;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private Integer quantity;
    private ProductDTO product;
    private Double priceAtAdd;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}