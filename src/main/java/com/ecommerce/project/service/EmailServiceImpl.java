package com.ecommerce.project.service;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
//    @Override
//    public void sendVerificationMail(String to, String subject, String url) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText("Verification Link  : " + url);
//            message.setFrom("shopease.india.official@gmail.com");
//            mailSender.send(message);
//
//        }
//        catch (Exception e) {
//            System.out.println("Some Error in sending mail");
//            System.out.println(e.getMessage());
//        }
//    }
    @Override
    public void sendVerificationMail(String to, String subject, String url) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("shopease.india.official@gmail.com", "ShopEase India");

            String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    @media only screen and (max-width: 600px) {
                        .inner-table { width: 100%% !important; border-radius: 0 !important; }
                        .button { width: 100%% !important; text-align: center !important; }
                    }
                </style>
            </head>
            <body style="margin:0; padding:0; background-color: #f1f5f9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #f1f5f9; padding: 40px 10px;">
                    <tr>
                        <td align="center">
                            <table class="inner-table" width="550" border="0" cellspacing="0" cellpadding="0" style="background-color: #ffffff; border-radius: 24px; overflow: hidden; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);">
                                
                               
                                        <tr>
                                               <td align="center" style="padding: 40px 0 20px 0;">
                                               <div style="background-color: #eff6ff; width: 60px; height: 60px; line-height: 60px; border-radius: 18px; display: inline-block; text-align: center;">
                                            <span style="font-size: 30px;">🛍️</span>
                                        </div>
                                            <div style="display: inline-block; font-size: 28px; font-weight: 900; letter-spacing: -0.05em; color: #2563eb; text-decoration: none;">
                                                    ShopEase
                                            </div>
                                            </td>
                                        </tr>

                                <tr>
                                    <td style="padding: 0 40px 30px 40px; text-align: center;">
                                        <h2 style="color: #1e293b; font-size: 20px; margin-bottom: 10px;">Verify your email ✅</h2>
                                        <p style="color: #64748b; font-size: 15px; line-height: 1.6; margin: 0;">
                                            To finalize your registration and start shopping, please confirm your email address by clicking the button below.
                                        </p>
                                    </td>
                                </tr>

                                <tr>
                                    <td align="center" style="padding: 0 40px 40px 40px;">
                                        <a href="%s" class="button"
                                           style="background-color: #2563eb; color: #ffffff; 
                                           padding: 16px 32px; text-decoration: none; 
                                           border-radius: 14px; display: inline-block; 
                                           font-weight: 700; font-size: 16px; 
                                           box-shadow: 0 4px 10px rgba(37, 99, 235, 0.2);">
                                           Confirm Email Address
                                        </a>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 30px 40px; background-color: #f8fafc; text-align: center; border-top: 1px solid #f1f5f9;">
                                        <p style="margin: 0; color: #94a3b8; font-size: 12px;">
                                            This link will expire in 24 hours. If you did not sign up for a ShopEase account, please ignore this email.
                                        </p>
                                        <div style="margin-top: 15px; border-top: 1px solid #e2e8f0; padding-top: 15px;">
                                            <p style="margin: 0; color: #64748b; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 1px;">
                                                &copy; 2025 ShopEase 
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.formatted(url);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("ShopEase Mailer Error: " + e.getMessage());
        }
    }






}