package com.ecommerce.project.service;

public interface SessionService {
    Boolean invalidateSession(String sessionId);
    Boolean validateSessionId(String rawRefreshToken, String sessionId);
}
