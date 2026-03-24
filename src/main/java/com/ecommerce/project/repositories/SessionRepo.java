package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepo extends JpaRepository<Session, Long> {
    List<Session> findByUserUserIdAndActiveTrue(Long userId);
}
