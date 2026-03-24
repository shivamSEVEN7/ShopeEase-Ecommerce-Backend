package com.ecommerce.project.service;

import com.ecommerce.project.model.RefreshToken;
import com.ecommerce.project.model.Session;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private HmacService hmacService;

    @Override
    public RefreshToken generateRefreshToken(String rawToken, String username, Session session) {
//        long validity = 30 * 24 * 60 * 60 * 1000L;
        String encryptedToken = hmacService.hash(rawToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .hashedRefreshToken(encryptedToken)
                .expiry(OffsetDateTime.now().plusDays(7))
                .user(userRepo.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found")))
                .session(session)
                .used(false)
                .revoked(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        return refreshToken;
    }

    @Override
    public Boolean validateRefreshToken(String hashedRefreshToken, String sessionId) {
        Optional<RefreshToken> tokenOpt =
                refreshTokenRepo.findByHashedRefreshToken(hashedRefreshToken);
        if (tokenOpt.isEmpty()){
            System.out.println("Empty Token");
            return false;}
        RefreshToken token = tokenOpt.get();

        if (!token.getSession().getSessionId().equals(sessionId)) {
            System.out.println("Session id not matching");
            return false;
        }
        if (!token.getSession().isActive()) {
            System.out.println("Session not active");
            return false;
        }

        if (token.isRevoked()) {
            System.out.println("Token Revoked");
            return false;
        }

        if (token.getExpiry().isBefore(OffsetDateTime.now())) {
            System.out.println("Expired token");
           return false;
        }
        return true;
    }

    @Override
    public RefreshToken rotateRefreshTokens(RefreshToken oldRefreshToken, String rawRefreshToken) {
        OffsetDateTime now = OffsetDateTime.now();
        oldRefreshToken.setUsed(true);
        oldRefreshToken.setRotatedAt(now);
        oldRefreshToken.setGraceUntil(now.plusSeconds(60));
        RefreshToken updatedRefreshToken = generateRefreshToken(rawRefreshToken, oldRefreshToken.getUser().getUsername(), oldRefreshToken.getSession());
        updatedRefreshToken = refreshTokenRepo.save(updatedRefreshToken);
        if(oldRefreshToken.getReplacedBy()==null){
        oldRefreshToken.setReplacedBy(updatedRefreshToken);
        }
        refreshTokenRepo.save(oldRefreshToken);
        return updatedRefreshToken;
    }

    @Override
    public void invalidateRefreshToken(String refreshToken, String sessionId) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByHashedRefreshToken(hmacService.hash(refreshToken));
            RefreshToken refreshToken1 = refreshTokenOpt.get();
            refreshToken1.setRevoked(true);
            refreshTokenRepo.save(refreshToken1);
    }

    @Override
    public void invalidateRefreshTokenOtherDevice(String sessionId) {
        RefreshToken refreshToken = refreshTokenRepo.findBySession_SessionId(sessionId);
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
