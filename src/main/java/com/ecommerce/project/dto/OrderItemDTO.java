package com.ecommerce.project.dto;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private String referenceOrderId;
    private ProductDTO product;
    private Integer quantity;
    private double unitPriceAtPurchase;
    private double unitListingPriceAtPurchase;
    private double priceAtPurchase;
    private double totalAmountAtPurchase;
    private double discount;
    private ReviewResponseDTO review;
}
