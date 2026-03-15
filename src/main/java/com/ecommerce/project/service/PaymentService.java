package com.ecommerce.project.service;

import com.cashfree.pg.model.PaymentEntity;
import com.ecommerce.project.dto.PaymentDTO;
import com.ecommerce.project.dto.PaymentInitiationDTO;
import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.Payment;
import com.ecommerce.project.model.PaymentStatus;

public interface PaymentService {
    String initiateOnlinePayment(Order order, String paymentMethod);
    Order initiateCodPayment(Order order, String paymentMethod);
    void updatePaymentSuccess(String payload);
    void updatePaymentSuccess(PaymentEntity paymentEntity, Order order);
    void updatePaymentFailure(String payload, PaymentStatus paymentStatus);
    Payment getLastPaymentAttempt(String referenceOrderId);
}
