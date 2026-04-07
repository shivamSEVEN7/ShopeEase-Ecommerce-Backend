package com.ecommerce.project.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.OffsetDateTime;

@Data
public class AdRequest {

    private String title;
    private String subtitle;
    private String description;

    private String redirectUrl;

    // 🔥 Files instead of URL
    private MultipartFile productImage;
    private MultipartFile bannerImage;

    private String backgroundColor;
    private String ctaText;

    private int priority;
    private boolean active;

    private OffsetDateTime expiryAt;

    private Long productId;
    private AdType type;
}