package com.ecommerce.project.dto;

import com.ecommerce.project.model.SellerStatus;
import com.ecommerce.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.OffsetDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerDetailsDTO {
    private Long id;
    private String sellerCode;
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private String gstNumber;
    private String panNumber;
    private String bankAccountNumber;
    private String ifscCode;
    private SellerStatus status;
    private OffsetDateTime createdAt;
}
