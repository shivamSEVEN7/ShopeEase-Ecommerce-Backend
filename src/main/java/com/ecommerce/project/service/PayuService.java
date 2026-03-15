package com.ecommerce.project.service;


import com.ecommerce.project.dto.PaymentInitiationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@Service
public class PayuService {
    @Value("${payu.merchant.key}")
    private String key;
    @Value("${payu.merchant.salt}")
    private String salt;

    public PaymentInitiationDTO initiatePayment(Map<String, String> params){
        String txnid = generateTransactionId();
        params.put("txnid", txnid);
        String hash = generateHash(params);
        String surl = "http://localhost:8080/api/order/payment/success";
        String furl = "http://localhost:8080/api/order/payment/failed";
        return new PaymentInitiationDTO(key, hash, txnid, ((int)Double.parseDouble(params.get("amount")))*100, params.get("firstname"),params.get("productinfo"), params.get("phone"), params.get("email"), surl, furl);
    }
    public String generateHash(Map<String, String> params) {
            // Extract parameters or use empty string if not provided
            String txnid = params.get("txnid");
            String amount = params.get("amount");
            String productinfo = params.get("productinfo");
            String firstname = params.get("firstname");
            String email = params.get("email");
            String udf1 = params.getOrDefault("udf1", "");
            String udf2 = params.getOrDefault("udf2", "");
            String udf3 = params.getOrDefault("udf3", "");
            String udf4 = params.getOrDefault("udf4", "");
            String udf5 = params.getOrDefault("udf5", "");

            // Construct hash string with exact parameter sequence
            String hashString = key + "|" + txnid + "|" + amount + "|" + productinfo + "|" +
                    firstname + "|" + email + "|" + udf1 + "|" + udf2 + "|" +
                    udf3 + "|" + udf4 + "|" + udf5 + "||||||" + salt;
            return sha512(hashString);
        }

        private static String sha512(String input) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString().toLowerCase();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    public static String generateTransactionId() {
        String timestampPart = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String uuidPart = UUID.randomUUID().toString();
        String randomPart = uuidPart.substring(0, 8);
        return "TXN-" + timestampPart + "-" + randomPart;
    }
    }

