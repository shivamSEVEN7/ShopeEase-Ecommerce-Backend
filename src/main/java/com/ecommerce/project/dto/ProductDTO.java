package com.ecommerce.project.dto;

import com.ecommerce.project.model.Category;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String productName;
    private String description;
    private String image;
    private int quantity;
    private Double price;
    private double SpecialPrice;
    private double discount;
    private String slug;
    private CategoryDTO category;
    private SellerResponseDTO seller;


}
