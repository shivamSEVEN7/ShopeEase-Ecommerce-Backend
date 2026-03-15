package com.ecommerce.project.repositories;

import com.ecommerce.project.model.OrderStatus;
import com.ecommerce.project.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.project.model.Order;


import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    Order findByOrderIdAndUser(String orderId, User user);

    Order findByOrderId(String orderId);

    Page<Order> findAllByUser(User user, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);
}