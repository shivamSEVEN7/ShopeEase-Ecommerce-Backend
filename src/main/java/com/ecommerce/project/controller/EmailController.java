package com.ecommerce.project.controller;

import com.ecommerce.project.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestParam String to) {
        emailService.sendMail(to, "Welcome to ShopEase India",
                "<h2>Welcome!</h2><p>Thank you for signing up.</p>");
        return ResponseEntity.ok("Mail sent successfully!");
    }
}
