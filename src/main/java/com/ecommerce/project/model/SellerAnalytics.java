package com.ecommerce.project.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
public class SellerAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    private int totalOrders;
    private BigDecimal totalRevenue;
    private int totalProductsSold;
    private int totalCancelled;
    private double averageRating;

    private OffsetDateTime updatedAt;
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
