package com.ecommerce.project.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reviews")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(length = 1000)
    private String comment;

    @Column(length = 100)
    private String title;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Review(Product product, User user, OrderItem orderItem, BigDecimal rating, String comment, String title) {
        this.product = product;
        this.user = user;
        this.orderItem = orderItem;
        this.rating = rating;
        this.comment = comment;
        this.title = title;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

}

