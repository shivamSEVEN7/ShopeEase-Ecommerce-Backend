package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepo extends JpaRepository<Image, Long> {
}
