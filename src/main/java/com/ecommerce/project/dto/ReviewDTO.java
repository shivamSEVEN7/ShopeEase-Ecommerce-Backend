package com.ecommerce.project.dto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ReviewDTO {
    private Long id;
    private BigDecimal rating;
    private String title;
    private String comment;
    private String createdAt;
    private UserDTO user;
    private Long productId;

}
