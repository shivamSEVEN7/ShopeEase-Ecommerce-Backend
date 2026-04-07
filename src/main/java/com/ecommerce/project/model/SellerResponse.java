package com.ecommerce.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponse {
    private Long id;
    private String sellerCode;
    private String businessName;
}
