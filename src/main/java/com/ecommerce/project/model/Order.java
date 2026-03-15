package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", unique = true)
    private String orderId;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;
    private double subTotal;
    private double discount;
    private double shipping;
    @Column(name = "total_amount", nullable = false)
    private double totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    @Email
    @Column(name = "user_email", nullable = false)
    private String email;

    @OneToOne(mappedBy = "order", cascade =  CascadeType.ALL)
    private ShippingAddress shippingAddress;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER) // "mappedBy" points to the 'order' field in the Payment entity
    private List<Payment> payment = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public Order(String orderId, OrderStatus status, double totalAmount, List<OrderItem> orderItems, String email) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
        this.email = email;



    }
}
