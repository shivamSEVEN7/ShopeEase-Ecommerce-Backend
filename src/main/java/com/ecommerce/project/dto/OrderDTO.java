package com.ecommerce.project.dto;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.OrderStatus;
import com.ecommerce.project.model.Payment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderId;
    private OffsetDateTime createdAt;
    private OrderStatus status;
    private double subTotal;
    private double discount;
    private double shipping;
    private double totalAmount;
    private String email;
    private ShippingAddressDTO shippingAddress;
    private List<OrderItemDTO> orderItems;
    private PaymentDTO payment;
}
