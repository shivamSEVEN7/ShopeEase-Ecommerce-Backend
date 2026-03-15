package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.OrderItemRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart();
    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, int quantity);
    @Transactional
    CartDTO deleteProductFromCart(Long productId);
    @Transactional
    void cleanupCart(List<OrderItemRequestDTO> orderItemDTOS);
}
