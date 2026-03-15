package com.ecommerce.project.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationDTO {
    private String key;
    private String hash;
    private String txnid;
    private double amount;
    private String firstname;
    private String productinfo;
    private String phone;
    private String email;
    private String surl;
    private String furl;

}
