package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String publicId;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private OffsetDateTime createdAt;
    private Boolean isPrimary = false;
    @PostPersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public Image(Product product, String publicId, String url, Boolean isPrimary) {
        this.publicId = publicId;
        this.url = url;
        this.product = product;
        this.isPrimary = isPrimary;
    }

    public Image(String publicId, String url, Boolean isPrimary) {
        this.publicId = publicId;
        this.url = url;
        this.product = product;
        this.isPrimary = isPrimary;
    }
}
