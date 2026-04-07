package com.ecommerce.project.model;
import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "advertisements")
@Getter
@Setter
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;

    @Column(length = 500)
    private String description;

    private String redirectUrl;

    private String productImage;
    private String bannerImage;

    private String backgroundColor;

    private String ctaText;

    private int priority;

    // Optional relation
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Status
    private boolean active;
    @Enumerated(EnumType.STRING)
    private AdType type;
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;


    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime expiryAt;

    public Ad() {}


    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

}