package com.ecommerce.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.project.model.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long>{
    @Query("SELECT p from Payment p WHERE p.order.orderId = ?1")
    Payment findByOrderId(Long orderId);

    Payment findFirstByReferenceOrderId(String referenceOrderId);

    Payment findFirstByReferenceOrderIdOrderByPaymentTimeDesc(String referenceOrderId);

    Optional<Payment> findFirstByReferenceOrderIdOrderByCreatedAtDesc(String referenceOrderId);
}
