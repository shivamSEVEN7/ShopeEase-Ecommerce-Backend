package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
        private Long id;
        private BigDecimal rating;
        private String title;
        private String comment;
        private String createdAt;
        private UserDTO user;
        private Long productId;

}
