package com.ecommerce.project.controller;

import com.ecommerce.project.security.request.OtpLoginRequest;
import com.ecommerce.project.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class OtpController {
    @Autowired
    OtpService otpService;
    @PostMapping("/otp/generate")
    public ResponseEntity<String> generateOtp(@RequestBody OtpLoginRequest loginRequest) {
        System.out.println("Request Received");
        return new ResponseEntity<>(otpService.generateOtp(loginRequest.getEmail(), "login"), HttpStatus.OK);
    }

}
