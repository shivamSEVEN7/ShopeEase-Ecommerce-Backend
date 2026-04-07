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
    @Column(nullable = false, unique = true)
    private String hashedRefreshToken;
    private OffsetDateTime expiry;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "sessionId")
    private Session session;
    private OffsetDateTime rotatedAt;
    private OffsetDateTime graceUntil;
    @OneToOne
    @JoinColumn(name = "replaced_by_id")
    private RefreshToken replacedBy;
    private boolean revoked = false;
    @Column(nullable = false)
    private boolean used = false;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public boolean isExpired() {
        return expiry.isBefore(OffsetDateTime.now());
    }

    public boolean isWithinGracePeriod() {
        return graceUntil != null &&
                OffsetDateTime.now().isBefore(graceUntil);
    }

    public boolean isUsable() {
        if (revoked || isExpired()) return false;
        if (!used) return true;
        return isWithinGracePeriod();
    }
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
