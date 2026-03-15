package com.ecommerce.project.dto;

import com.ecommerce.project.model.ProductHighlightRequestDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ProductRequestDTO {
    private String productName;
    private String description;
    private int quantity;
    private Double price;
    private double discount;
    private List<ProductHighlightRequestDTO> highlights;

}
