package com.ecommerce.project.service;
import com.cashfree.pg.ApiResponse;
import com.cashfree.pg.*;
import com.cashfree.pg.model.*;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.utility.AuthUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
public class CashFreeService {
    @Value("${cashfree.id}")
    private String clientId;

    @Value("${cashfree.secret}")
    private String clientSecret;
    @Autowired
    AuthUtil authUtil ;
    Cashfree cashfree;
    @PostConstruct
    public void init() {
        this.cashfree = new Cashfree(
                Cashfree.CFEnvironment.SANDBOX,
                clientId,
                clientSecret,
                "",
                "",
                ""
        );
    }

    String createOrder(Double orderAmount, String orderId) {
        String paymentSessionId;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderAmount(BigDecimal.valueOf(orderAmount).setScale(2, RoundingMode.HALF_UP));
        request.setOrderCurrency("INR");
        request.setOrderId(orderId);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerId(String.valueOf(authUtil.loggedInUserId()));
        customerDetails.setCustomerPhone(authUtil.loggedInUser().getMobileNumber());
        customerDetails.setCustomerName(authUtil.loggedInUser().getName());
        customerDetails.setCustomerEmail(authUtil.loggedInEmail());
        request.setCustomerDetails(customerDetails);

        OrderMeta orderMeta = new OrderMeta();

        request.setOrderMeta(orderMeta);
        request.setOrderExpiryTime(Instant.now().plusSeconds(86400).toString());


        try {
            ApiResponse<OrderEntity> response = cashfree.PGCreateOrder(request, null, null, null);
            paymentSessionId =  response.getData().getPaymentSessionId();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return paymentSessionId;
    }

    public String fetchPaymentSessionId(String orderId) {
        ApiResponse<OrderEntity> apiResponse = null;
        try {
           apiResponse =  cashfree.PGFetchOrder(orderId, null, null, null);
        }
        catch (ApiException e){
            System.out.println("Some exception occurred in fetching order " + e.getMessage());
        }
        return apiResponse.getData().getPaymentSessionId();
    }

    public String fetchOrderStatus(String orderId) {
        ApiResponse<OrderEntity> apiResponse = null;
        try {
            apiResponse =  cashfree.PGFetchOrder(orderId, null, null, null);
        }
        catch (ApiException e){
            System.out.println("Some exception occurred in fetching order " + e.getMessage());
        }
        return apiResponse.getData().getOrderStatus();
    }

    public PaymentEntity fetchSuccessfulPayment(String orderId) {
        ApiResponse<List<PaymentEntity>> apiResponse = null;

        try {
            apiResponse =  cashfree.PGOrderFetchPayments(orderId, null, null, null);

        }
        catch (ApiException e){
            System.out.println("Some exception occurred in fetching order " + e.getMessage());
        }
        List<PaymentEntity> paymentEntities = apiResponse.getData();
        PaymentEntity successfulPayment = paymentEntities.stream().filter(paymentEntity -> paymentEntity.getPaymentStatus().equals(PaymentEntity.PaymentStatusEnum.SUCCESS)).findFirst().orElseThrow(() -> new APIException("No Successful Payment found for this Order"));
        return successfulPayment;
    }

    public String generateSignature(String timestamp, String payload) {

        String computed_signature = "";
        try{
            String data = timestamp+payload;
            String secretKey = clientSecret; // Get secret key from Cashfree Merchant Dashboard;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key_spec = new SecretKeySpec(secretKey.getBytes(),"HmacSHA256");
            sha256_HMAC.init(secret_key_spec);
            computed_signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
        }
        catch (Exception e){
            System.out.println("Exception Occured in verifying Signature " + e.getMessage());
        }

        return computed_signature;
    }


    public static String generateCashfreeOrderId() {
        String timestampPart = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "CFORD-" + timestampPart + "-" + randomPart;
    }

}
