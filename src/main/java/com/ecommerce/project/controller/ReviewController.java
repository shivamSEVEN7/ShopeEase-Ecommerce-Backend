package com.ecommerce.project.controller;


import com.ecommerce.project.dto.ReviewDTO;
import com.ecommerce.project.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReviewController {
    ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("order-item/{orderItemId}/review")
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO review, @PathVariable long orderItemId) {
        ReviewDTO reviewDTO =  reviewService.addReview(review,orderItemId);
        return ResponseEntity.ok().body(Map.of("message", "Review has been added successfully", "review", reviewDTO));
    }
}
