package com.ecommerce.project.service;

import com.ecommerce.project.model.RefreshToken;
import com.ecommerce.project.model.Session;
import com.ecommerce.project.repositories.RefreshTokenRepo;
import com.ecommerce.project.repositories.SessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionServiceImpl implements SessionService{
    @Autowired
    SessionRepo sessionRepo;
    @Autowired
    RefreshTokenRepo refreshTokenRepo;
    @Autowired
    HmacService hmacService;
    @Override
    public Boolean invalidateSession(String sessionId) {
        int numberOfRefreshTokensInvoked = refreshTokenRepo.deleteBySession_SessionId(sessionId);
        Session activeSession = sessionRepo.findBySessionId(sessionId);
        activeSession.setActive(false);
        sessionRepo.save(activeSession);
        return numberOfRefreshTokensInvoked > 0;
    }
    @Override
    public Boolean validateSessionId(String rawRefreshToken, String sessionId){
        String hashedRefreshToken = hmacService.hash(rawRefreshToken);
        Optional<RefreshToken> token =  refreshTokenRepo.findByHashedRefreshToken(hashedRefreshToken);
        if (token.isEmpty()){
            return false;
        }
        else {
            return token.get().getSession().getSessionId().equals(sessionId);
        }
    }
}

