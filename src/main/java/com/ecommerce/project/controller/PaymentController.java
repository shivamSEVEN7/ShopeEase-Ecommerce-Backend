package com.ecommerce.project.controller;

import com.ecommerce.project.dto.PaymentDTO;
import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.model.Payment;
import com.ecommerce.project.model.PaymentStatus;
import com.ecommerce.project.service.CashFreeService;
import com.ecommerce.project.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    PaymentService paymentService;
    @Autowired
    private CashFreeService cashFreeService;


    @PostMapping("/payments/webhooks/success")
    public ResponseEntity<String> verifyPaymentSuccess(@RequestBody String payload,
                                                      @RequestHeader("x-webhook-signature") String signature,
                                                      @RequestHeader("x-webhook-timestamp") String timestamp){
//        System.out.println("Payment SuccessFul");
        String computedSignature = cashFreeService.generateSignature(timestamp, payload);
 //       System.out.println("Payment Successful and Computed signature is " + computedSignature + " Original Signature is " + signature);
        if(computedSignature.equals(signature)){
            paymentService.updatePaymentSuccess(payload);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);

    }
    @PostMapping("/payments/webhooks/failure")
    public ResponseEntity<String> verifyPaymentFailure(@RequestBody String payload,
                                                       @RequestHeader("x-webhook-signature") String signature,
                                                       @RequestHeader("x-webhook-timestamp") String timestamp){
        String computedSignature = cashFreeService.generateSignature(timestamp, payload);
        System.out.println("Payment Failed and Computed signature is " + computedSignature + " Original Signature is " + signature);
        if(computedSignature.equals(signature)){
            paymentService.updatePaymentFailure(payload, PaymentStatus.FAILED);
        }
        return new ResponseEntity<>("Failed", HttpStatus.OK);
    }

    @PostMapping("/payments/webhooks/user-dropped")
    public ResponseEntity<String> userDropped(@RequestBody String payload,
                                                @RequestHeader("x-webhook-signature") String signature,
                                                @RequestHeader("x-webhook-timestamp") String timestamp){

        System.out.println("User Dropped and Payment Failed");
        String generateSignature = cashFreeService.generateSignature(timestamp, payload);
        System.out.println("Payment Failed due to User Dropped and Computed signature is " + generateSignature + " Original Signature is " + signature);
        if(generateSignature.equals(signature)){
            paymentService.updatePaymentFailure(payload, PaymentStatus.CANCELLED);
        }
        return new ResponseEntity<>("Failed", HttpStatus.OK);
    }
    @PostMapping("/payments/notify")
    public ResponseEntity<String> notifyPayment(@RequestBody String payload,
                                                @RequestHeader("x-webhook-signature") String signature,
                                                @RequestHeader("x-webhook-timestamp") String timestamp){
        System.out.println("Payment Notification Recievd");
        return new ResponseEntity<>("Failed", HttpStatus.OK);
    }

    @PostMapping("/payments/retry")
    public ResponseEntity<String> retryPayment(@RequestParam String orderId){

        String paymentSessionId = cashFreeService.fetchPaymentSessionId(orderId);
        return new ResponseEntity<>(paymentSessionId, HttpStatus.OK);
    }

}
