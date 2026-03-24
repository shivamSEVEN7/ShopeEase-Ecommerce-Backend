package com.ecommerce.project.repositories;

import com.ecommerce.project.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    RefreshToken findBySession_SessionId(String sessionId);

    List<RefreshToken> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);

    Optional<RefreshToken> findByHashedRefreshToken(String hashedRefreshToken);
}
