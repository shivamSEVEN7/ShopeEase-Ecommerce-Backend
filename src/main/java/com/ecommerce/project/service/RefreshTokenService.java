package com.ecommerce.project.service;

import com.ecommerce.project.model.RefreshToken;
import com.ecommerce.project.model.Session;
import com.ecommerce.project.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(String rawToken, String username, Session session);

    Boolean validateRefreshToken(String hashedRefreshToken, String sessionId);

    RefreshToken rotateRefreshTokens(RefreshToken oldRefreshToken, String rawRefreshToken);
    RefreshToken rotateRefreshTokensWithinGrace(RefreshToken oldRefreshToken, String rawRefreshToken);
    void invalidateRefreshToken(String refreshToken, String sessionId);
    void invalidateRefreshTokenOtherDevice(String sessionId);

    List<RefreshToken> fetchRefreshTokensOfUser(Long userId);
}
