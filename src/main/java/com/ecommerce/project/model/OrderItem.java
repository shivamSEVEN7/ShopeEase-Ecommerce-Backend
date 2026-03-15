package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "reference_order_id")
    private String referenceOrderId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
    private double unitPriceAtPurchase;
    private double unitListingPriceAtPurchase;
    @Column(name = "price_at_purchase", nullable = false)
    private double priceAtPurchase;

    @Column(name = "total_amount_at_purchase", nullable = false)
    private double totalAmountAtPurchase;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;
    private double discount;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;

    public OrderItem(Order order, String referenceOrderId, Product product, Integer quantity, double unitListingPriceAtPurchase, double unitPriceAtPurchase, double priceAtPurchase, double amountAtPurchase, double discount) {
        this.order = order;
        this.referenceOrderId = referenceOrderId;
        this.product = product;
        this.quantity = quantity;
        this.unitListingPriceAtPurchase = unitListingPriceAtPurchase;
        this.unitPriceAtPurchase = unitPriceAtPurchase;
        this.priceAtPurchase = priceAtPurchase;
        this.totalAmountAtPurchase = amountAtPurchase;
        this.discount = discount;
    }
}