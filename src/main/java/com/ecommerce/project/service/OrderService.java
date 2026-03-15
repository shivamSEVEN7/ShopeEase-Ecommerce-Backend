package com.ecommerce.project.service;

import com.ecommerce.project.dto.*;
import com.ecommerce.project.model.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional
    CreateOrderResponse placeOrder(OrderRequestDTO orderRequest);

    OrderStatus getOrderStatus(String orderId);
    UserOrdersResponse getUserOrders(int page, int size, String sortBy, String sortOrder);

    OrderDTO getOrderByOrderId(String orderId);
    void cancelOrder(Long orderId);

    boolean isPaymentPending(Long orderId);
}
