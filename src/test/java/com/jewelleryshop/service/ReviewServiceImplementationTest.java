package com.jewelleryshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.Review;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.ProductRepository;
import com.jewelleryshop.repository.ReviewRepository;
import com.jewelleryshop.request.ReviewRequest;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplementationTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewServiceImplementation reviewService;

    private User user;
    private Product product;
    private ReviewRequest reviewRequest;

    @BeforeEach
    public void setUp() {
        // Set up mock user and product
        user = new User();
        user.setId(1L);
        user.setFirstName("John Doe");

        product = new Product();
        product.setId(1L);
        product.setTitle("Gold Ring");

        reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setReview("Great product!");
    }

    @Test
    public void testCreateReview_Success() throws ProductException {
        // Arrange: Mock product service to return the product
        when(productService.findProductById(reviewRequest.getProductId())).thenReturn(product);

        // Arrange: Mock review repository to return the saved review
        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setReview("Great product!");
        savedReview.setProduct(product);
        savedReview.setUser(user);
        savedReview.setCreatedAt(LocalDateTime.now());
        
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // Act: Create review via the service
        Review result = reviewService.createReview(reviewRequest, user);

        // Assert: Ensure the review was created as expected
        assertNotNull(result);
        assertEquals("Great product!", result.getReview());
        assertEquals(1L, result.getProduct().getId());
        assertEquals(1L, result.getUser().getId());
    }

    @Test
    public void testCreateReview_ProductNotFound() throws ProductException {
        // Arrange: Mock product service to throw exception
        when(productService.findProductById(reviewRequest.getProductId())).thenThrow(new ProductException("Product not found"));

        // Act & Assert: Assert that the exception is thrown
        ProductException exception = assertThrows(ProductException.class, () -> reviewService.createReview(reviewRequest, user));
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testGetAllReview_Success() {
        // Arrange: Mock reviews for a product
        Review review1 = new Review();
        review1.setReview("Great product!");
        review1.setProduct(product);
        review1.setUser(user);
        review1.setCreatedAt(LocalDateTime.now());

        Review review2 = new Review();
        review2.setReview("Decent product.");
        review2.setProduct(product);
        review2.setUser(user);
        review2.setCreatedAt(LocalDateTime.now());

        List<Review> reviews = List.of(review1, review2);
        when(reviewRepository.getAllProductsReview(product.getId())).thenReturn(reviews);

        // Act: Get reviews from the service
        List<Review> result = reviewService.getAllReview(product.getId());

        // Assert: Ensure the reviews are returned as expected
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Great product!", result.get(0).getReview());
        assertEquals("Decent product.", result.get(1).getReview());
    }

    @Test
    public void testGetAllReview_NoReviewsFound() {
        // Arrange: Mock no reviews for the product
        when(reviewRepository.getAllProductsReview(product.getId())).thenReturn(List.of());

        // Act: Get reviews from the service
        List<Review> result = reviewService.getAllReview(product.getId());

        // Assert: Ensure no reviews are returned
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
