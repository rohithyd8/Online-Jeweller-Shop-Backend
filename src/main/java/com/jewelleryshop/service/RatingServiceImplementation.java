package com.jewelleryshop.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.Rating;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.RatingRepository;
import com.jewelleryshop.request.RatingRequest;

@Service
public class RatingServiceImplementation implements RatingServices {

    private static final Logger logger = LoggerFactory.getLogger(RatingServiceImplementation.class);

    private RatingRepository ratingRepository;
    private ProductService productService;

    public RatingServiceImplementation(RatingRepository ratingRepository, ProductService productService) {
        this.ratingRepository = ratingRepository;
        this.productService = productService;
    }

    @Override
    public Rating createRating(RatingRequest req, User user) throws ProductException {
        logger.info("Creating rating for product ID: {} by user ID: {}", req.getProductId(), user.getId());

        Product product = productService.findProductById(req.getProductId());

        Rating rating = new Rating();
        rating.setProduct(product);
        rating.setUser(user);
        rating.setRating(req.getRating());
        rating.setCreatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);
        
        logger.info("Rating created successfully for product ID: {} with rating: {}", req.getProductId(), req.getRating());
        return savedRating;
    }

    @Override
    public List<Rating> getProductsRating(Long productId) {
        logger.info("Fetching ratings for product ID: {}", productId);

        List<Rating> ratings = ratingRepository.getAllProductsRating(productId);

        logger.info("Fetched {} ratings for product ID: {}", ratings.size(), productId);
        return ratings;
    }
}
