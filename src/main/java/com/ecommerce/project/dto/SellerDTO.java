package com.ecommerce.project.dto;

import com.ecommerce.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerDTO {
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private String gstNumber;
    private String panNumber;
    private String bankAccountNumber;
    private String ifscCode;
}
