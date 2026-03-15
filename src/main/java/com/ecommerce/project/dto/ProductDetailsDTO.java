package com.ecommerce.project.dto;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.ProductHighlightResponseDTO;
import com.ecommerce.project.model.Review;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDTO {
    private Long id;
    private String productName;
    private String description;
    private String image;
    private List<String> additionalImages = new ArrayList<>();
    private int quantity;
    private Double price;
    private double SpecialPrice;
    private double discount;
    private String slug;
    private CategoryDTO category;
    private List<ProductHighlightResponseDTO> highlights = new ArrayList<>();
    private SellerResponseDTO seller;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private List<ReviewDTO> reviews;
}
