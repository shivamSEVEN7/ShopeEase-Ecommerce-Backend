package com.ecommerce.project.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class HmacService {

    private final String secret;

    public HmacService(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String hash(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec key =
                    new SecretKeySpec(secret.getBytes(), "HmacSHA256");

            mac.init(key);

            byte[] raw = mac.doFinal(data.getBytes());

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(raw);

        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }
}
