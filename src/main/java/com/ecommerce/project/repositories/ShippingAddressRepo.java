package com.ecommerce.project.repositories;

import com.ecommerce.project.model.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingAddressRepo extends JpaRepository<ShippingAddress, Long> {

}
