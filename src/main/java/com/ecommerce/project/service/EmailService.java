package com.ecommerce.project.service;

public interface EmailService {
    void sendMail(String toEmail, String subject, String htmlContent);
    void sendOtpMail(String toEmail, String subject, String otp);
    void sendVerificationMail(String to, String subject, String url);

}
