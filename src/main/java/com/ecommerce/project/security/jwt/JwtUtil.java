package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET = "hiuhiosdhskinbhyi0jb19h0jgiasdh7h98jgh2";
    @Value("${jwt.expiration}")
    private long EXPIRATION ;
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    public String generateToken(String username){
        return  Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .subject(username)
                .signWith(key)
                .compact();
    }

    public String extractTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }

    public String extractUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public boolean verifyToken(String token, UserDetails userDetails, String username) {
        if(userDetails.getUsername().equals(username) && !isTokenExpired(token)){
            return true;
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return new Date().after(extractClaims(token).getExpiration());
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
       return WebUtils.getCookie(request, "JWT") == null ? null : WebUtils.getCookie(request, "JWT").getValue();
    }
}
