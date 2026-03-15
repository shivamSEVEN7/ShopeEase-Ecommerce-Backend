package com.ecommerce.project.model;

public enum PaymentStatus {
    INITIATED, // For online payments only
    UNPAID,    // COD when order placed
    PENDING,   // PAYMENT not Confirmed
    PAID,      // COD after delivery OR online success
    FAILED,
    CANCELLED,
    REFUNDED   // Money returned
}
