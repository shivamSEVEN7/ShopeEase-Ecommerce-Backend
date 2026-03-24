package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;


@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String deviceType;
    private String deviceInfo;
    private String ipAddress;

    @Column(nullable = false)
    private boolean active = true;

    private OffsetDateTime createdAt;
    private OffsetDateTime lastUsedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        lastUsedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUsedAt = OffsetDateTime.now();
    }
}