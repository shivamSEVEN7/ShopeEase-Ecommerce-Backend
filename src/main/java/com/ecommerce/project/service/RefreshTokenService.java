package com.ecommerce.project.service;

import com.ecommerce.project.model.RefreshToken;
import com.ecommerce.project.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RefreshTokenService {
    public RefreshToken generateRefreshToken(String rawToken, String username, HttpServletRequest request);

    RefreshToken validateRefreshToken(String refreshToken, String sessionId);

    RefreshToken refreshToken(RefreshToken oldRefreshToken, String rawRefreshToken);

    void invalidateRefreshToken(String refreshToken, String sessionId);
    void invalidateRefreshTokenOtherDevice(String sessionId);

    List<RefreshToken> fetchRefreshTokensOfUser(Long userId);
}
