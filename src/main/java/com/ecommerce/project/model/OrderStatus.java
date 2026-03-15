package com.ecommerce.project.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_FAILED,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    RETURNED,
    CANCELLED
}
