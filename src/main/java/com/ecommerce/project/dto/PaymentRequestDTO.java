package com.ecommerce.project.dto;

import lombok.Getter;

@Getter
public class PaymentRequestDTO {

    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
}
