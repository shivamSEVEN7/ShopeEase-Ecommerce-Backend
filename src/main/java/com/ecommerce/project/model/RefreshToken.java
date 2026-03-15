package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;
    private String hashedRefreshToken;
    private Instant expiry;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String sessionId;
    private String deviceType;
    private String deviceInfo;   // e.g. "Chrome on Windows 11"
    private String ipAddress;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

}
