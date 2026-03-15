package com.ecommerce.project.dto;
import com.ecommerce.project.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartId;
    private Double price = 0.0;
    private Double discount =0.0;
    private Double shipping = 0.0;
    private Double totalAmount=0.0;
    private List<CartItemDTO> cartItems = new ArrayList<>();
}
