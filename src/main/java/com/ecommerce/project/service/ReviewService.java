package com.ecommerce.project.service;


import com.ecommerce.project.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO addReview(ReviewDTO review, Long orderItemId);
}
