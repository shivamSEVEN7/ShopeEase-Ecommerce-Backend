package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Seller {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "seller_code", unique = true, nullable = false, updatable = false)
        private String sellerCode;

        @OneToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        private String businessName;
        private String businessEmail;
        private String businessPhone;

        private String gstNumber;
        private String panNumber;

        private String bankAccountNumber;
        private String ifscCode;

        @Enumerated(EnumType.STRING)
        private SellerStatus status;

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

