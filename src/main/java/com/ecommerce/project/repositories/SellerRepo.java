package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Seller;
import com.ecommerce.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepo extends JpaRepository<Seller, Long> {
    Seller findByUser(User user);
}
