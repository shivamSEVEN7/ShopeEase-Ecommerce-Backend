package com.ecommerce.project.service;

import com.ecommerce.project.dto.ReviewDTO;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.Review;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.OrderItemRepo;
import com.ecommerce.project.repositories.ProductRepo;
import com.ecommerce.project.repositories.ReviewRepo;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ReviewServiceImpl implements ReviewService {
    ReviewRepo reviewRepo;
    OrderItemRepo orderItemRepo;
    AuthUtil authUtil;
    ModelMapper modelMapper;
    ProductRepo productRepository;
    public ReviewServiceImpl(ReviewRepo reviewRepo, OrderItemRepo orderItemRepo, AuthUtil authUtil, ModelMapper modelMapper, ProductRepo productRepository) {
        this.reviewRepo = reviewRepo;
        this.orderItemRepo = orderItemRepo;
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }
    @Override
    public ReviewDTO addReview(ReviewDTO review, Long orderItemId) {
        String reviewTitle = review.getTitle();
        if (reviewTitle == null || reviewTitle.trim().isEmpty()) {
            BigDecimal r = review.getRating().setScale(1, RoundingMode.HALF_UP);
            switch (r.toString()) {
                case "5.0":
                    reviewTitle = "Excellent Product";
                    break;
                case "4.5":
                    reviewTitle = "Very Good Product";
                    break;
                case "4.0":
                    reviewTitle = "Good Product";
                    break;
                case "3.5":
                    reviewTitle = "Above Average";
                    break;
                case "3.0":
                    reviewTitle = "Average Experience";
                    break;
                case "2.5":
                    reviewTitle = "Below Average";
                    break;
                case "2.0":
                    reviewTitle = "Poor Product";
                    break;
                case "1.5":
                    reviewTitle = "Disappointing Product";
                    break;
                case "1.0":
                    reviewTitle = "Bad Experience";
                    break;
                case "0.5":
                    reviewTitle = "Terrible Product";
                    break;
                default:
                    reviewTitle = "No Rating Provided";
                    break;
            }
        }
        OrderItem orderItem = orderItemRepo.getById(orderItemId);
        User user = authUtil.loggedInUser();
        Review reviewEntity = new Review(orderItem.getProduct(), user, orderItem, review.getRating(), review.getComment(), reviewTitle);
        Product product = orderItem.getProduct();


        Review savedReview =  reviewRepo.save(reviewEntity);
        product.setReviewCount(product.getReviewCount() + 1);
        BigDecimal oldAvg = product.getAverageRating() != null ? product.getAverageRating() : BigDecimal.ZERO;
        int count = product.getReviewCount();
        BigDecimal total = oldAvg.multiply(BigDecimal.valueOf(count - 1));
        BigDecimal newAverage = total.add(review.getRating())
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        product.setAverageRating(newAverage);
        productRepository.save(product);
        return modelMapper.map(savedReview, ReviewDTO.class);
    }
}
