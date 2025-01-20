package com.jewelleryshop.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.Review;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.ProductRepository;
import com.jewelleryshop.repository.ReviewRepository;
import com.jewelleryshop.request.ReviewRequest;

@Service
public class ReviewServiceImplementation implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImplementation.class);

    private ReviewRepository reviewRepository;
    private ProductService productService;
    private ProductRepository productRepository;

    public ReviewServiceImplementation(ReviewRepository reviewRepository, ProductService productService, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    public Review createReview(ReviewRequest req, User user) throws ProductException {
        logger.info("Creating review for product ID: {} by user ID: {}", req.getProductId(), user.getId());

        Product product = productService.findProductById(req.getProductId());

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReview(req.getReview());
        review.setCreatedAt(LocalDateTime.now());

        // Save the product with the new review
        productRepository.save(product);

        Review savedReview = reviewRepository.save(review);

        logger.info("Review created successfully for product ID: {} by user ID: {}", req.getProductId(), user.getId());
        return savedReview;
    }

    @Override
    public List<Review> getAllReview(Long productId) {
        logger.info("Fetching all reviews for product ID: {}", productId);

        List<Review> reviews = reviewRepository.getAllProductsReview(productId);

        logger.info("Fetched {} reviews for product ID: {}", reviews.size(), productId);
        return reviews;
    }
}
