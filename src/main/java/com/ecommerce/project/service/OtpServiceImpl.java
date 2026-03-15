package com.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public String generateOtp(String email, String purpose) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        String key = "otp:" + purpose + ":" + email;
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(5));
        return otp;
    }
    @Override
    public boolean verifyOtp(String email, String purpose, String otp) {
        String key = "otp:" + purpose + ":" + email;
        String cachedOtp = redisTemplate.opsForValue().get(key);
        if (otp.equals(cachedOtp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
    @Override
    public String generateVerificationToken(String email){
        String existingToken = redisTemplate.opsForValue().get("user:" + email + ":token");
        if(existingToken != null){
            redisTemplate.delete("user:" + email + ":token");
            redisTemplate.delete(existingToken);
        }
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, email, Duration.ofHours(24));
        redisTemplate.opsForValue().set("user:" + email + ":token", token, Duration.ofHours(24));
        return token;
    }
    @Override
    public String verifyVerificationToken(String token){
        String userEmail = redisTemplate.opsForValue().get(token);
        if(userEmail != null){
            redisTemplate.delete("user:" + userEmail + ":token");
            redisTemplate.delete(token);
            return userEmail;
        }
    return null;
    }


}

