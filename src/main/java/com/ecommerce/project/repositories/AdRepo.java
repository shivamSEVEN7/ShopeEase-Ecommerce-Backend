package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Ad;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface AdRepo extends JpaRepository<Ad, Long> {
    @Query("""
    SELECT a FROM Ad a
    WHERE a.active = true
    AND (a.expiryAt IS NULL OR a.expiryAt > :now)
    ORDER BY a.priority DESC
""")
    List<Ad> findActiveAds(@Param("now") OffsetDateTime now);
}
