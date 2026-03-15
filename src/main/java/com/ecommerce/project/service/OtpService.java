package com.ecommerce.project.service;

public interface OtpService {
    String generateOtp(String email, String purpose);
    boolean verifyOtp(String email, String purpose, String otp);
    String generateVerificationToken(String email);
    String verifyVerificationToken(String token);
}
