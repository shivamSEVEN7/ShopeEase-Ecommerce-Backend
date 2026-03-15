package com.ecommerce.project.dto;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.PaymentMethod;
import com.ecommerce.project.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private String referenceOrderId;
    private String cashfreePaymentId;
    private String bankReference;
    private PaymentMethod method;
    private double amount;
    private String cashfreePaymentMessage;
    private PaymentStatus status;
    private OffsetDateTime paymentTime;
}
