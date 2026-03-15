package com.ecommerce.project.service;

import com.ecommerce.project.model.RefreshToken;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RefreshTokenRepo;
import com.ecommerce.project.repositories.UserRepo;
import com.ecommerce.project.utility.AuthUtil;
import com.ecommerce.project.utility.ClientInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenImpl implements RefreshTokenService{
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ClientInfoUtil clientInfoUtil;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;
    @Autowired
    private AuthUtil authUtil;

    @Override
    public RefreshToken generateRefreshToken(String rawToken, String username, HttpServletRequest request) {
        long validity = 30 * 24 * 60 * 60 * 1000L;

        String encryptedToken = passwordEncoder.encode(rawToken);
        String sessionId = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                        .hashedRefreshToken(encryptedToken)
                        .expiry(Instant.now().plusMillis(validity))
                        .user(userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found")))
                        .sessionId(sessionId)
                        .deviceType(clientInfoUtil.getDeviceType(request))
                        .deviceInfo(clientInfoUtil.getClientDeviceInfo(request))
                        .ipAddress(clientInfoUtil.getClientIpAddress(request))
                        .build();

        return refreshToken;

    }

    @Override
    public RefreshToken validateRefreshToken(String refreshToken, String sessionId) {
        RefreshToken oldRefreshToken = refreshTokenRepo.findBySessionId(sessionId);
        if(oldRefreshToken!=null){
            if(oldRefreshToken.getExpiry().isAfter(Instant.now()) && passwordEncoder.matches(refreshToken, oldRefreshToken.getHashedRefreshToken())){
                return oldRefreshToken;
            }
        }
        return null;
    }

    @Override
    public RefreshToken refreshToken(RefreshToken oldRefreshToken, String rawRefreshToken) {

        oldRefreshToken.setHashedRefreshToken(passwordEncoder.encode(rawRefreshToken));
            RefreshToken updatedRefreshToken = refreshTokenRepo.save(oldRefreshToken);
            return updatedRefreshToken;
    }

    @Override
    public void invalidateRefreshToken(String refreshToken, String sessionId) {
            RefreshToken oldRefreshToken = validateRefreshToken(refreshToken, sessionId);
            refreshTokenRepo.delete(oldRefreshToken);
    }

    @Override
    public void invalidateRefreshTokenOtherDevice(String sessionId) {
        RefreshToken refreshToken = refreshTokenRepo.findBySessionId(sessionId);
        if(refreshToken.getUser().getUserId() == authUtil.loggedInUserId()){
            refreshTokenRepo.delete(refreshToken);
        }
    }

    @Override
    public List<RefreshToken> fetchRefreshTokensOfUser(Long userId){
        List<RefreshToken> refreshTokens = refreshTokenRepo.findAllByUser_UserIdOrderByCreatedAtDesc(userId);
        return refreshTokens;
    }


}
