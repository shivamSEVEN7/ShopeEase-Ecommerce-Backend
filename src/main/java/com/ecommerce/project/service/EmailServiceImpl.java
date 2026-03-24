package com.ecommerce.project.service;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText("text");
            message.setFrom("shopease.india.official@gmail.com");
            mailSender.send(message);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void sendOtpMail(String to, String subject, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText("Your OTP is : " + otp);
            message.setFrom("shopease.india.official@gmail.com");
            mailSender.send(message);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void sendVerificationMail(String to, String subject, String url) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText("Verification Link  : " + url);
            message.setFrom("shopease.india.official@gmail.com");
            mailSender.send(message);

        }
        catch (Exception e) {
            System.out.println("Some Error in sending mail");
            System.out.println(e.getMessage());
        }
    }






}