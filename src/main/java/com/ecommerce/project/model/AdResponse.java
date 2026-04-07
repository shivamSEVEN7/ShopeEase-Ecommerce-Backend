package com.ecommerce.project.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class AdResponse {

    private Long id;

    private String title;
    private String subtitle;
    private String description;

    private String redirectUrl;

    private String productImage;
    private String bannerImage;

    private String backgroundColor;
    private String ctaText;

    private int priority;
    private boolean active;

    private OffsetDateTime createdAt;
    private OffsetDateTime expiryAt;

    private AdType type;

    private SellerResponse seller;

    private Integer productId; // optional
}