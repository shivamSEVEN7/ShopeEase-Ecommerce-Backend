package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "reference_order_id")
    private String referenceOrderId;

    @Column(name = "cashfree_payment_id")
    private String cashfreePaymentId;

    @Column(name = "bank_reference")
    private String bankReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod method;

    @Column(nullable = false)
    private double amount;


    @Column(length = 510, name = "cashfree_payment_message", columnDefinition = "TEXT")
    private String cashfreePaymentMessage;


    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @Column(name = "payment_time")
    private OffsetDateTime paymentTime;
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

    public Payment(double amount, PaymentStatus status, String referenceOrderId, Order order) {
//        this.paymentGatewayTxnId = paymentGatewayTxnId;
        this.amount = amount;
        this.status = status;
        this.referenceOrderId = referenceOrderId;
        this.order = order;
    }

    public Payment(String cashfreePaymentId, String bankReference, PaymentMethod method, double amount, String cashfreePaymentMessage, PaymentStatus status, Order order, OffsetDateTime paymentTime, String referenceOrderId) {
        this.cashfreePaymentId = cashfreePaymentId;
        this.bankReference = bankReference;
        this.method = method;
        this.amount = amount;
        this.cashfreePaymentMessage = cashfreePaymentMessage;
        this.status = status;
        this.order = order;
        this.paymentTime = paymentTime;
        this.referenceOrderId = referenceOrderId;
    }
}
