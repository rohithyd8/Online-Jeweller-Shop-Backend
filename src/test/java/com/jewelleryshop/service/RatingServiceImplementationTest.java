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
import com.jewelleryshop.modal.Rating;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.RatingRepository;
import com.jewelleryshop.request.RatingRequest;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplementationTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private RatingServiceImplementation ratingService;

    private User user;
    private Product product;
    private RatingRequest ratingRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L); 
        user.setFirstName("John Doe");

        product = new Product();
        product.setId(1L); 
        product.setTitle("Gold Ring");

        ratingRequest = new RatingRequest();
        ratingRequest.setProductId(product.getId()); 
        ratingRequest.setRating(4.5); 
    }

    @Test
    public void testCreateRating_Success() throws ProductException {
        when(productService.findProductById(ratingRequest.getProductId())).thenReturn(product);
        Rating savedRating = new Rating();
        savedRating.setId(1L);
        savedRating.setProduct(product);
        savedRating.setUser(user);
        savedRating.setRating(ratingRequest.getRating());
        savedRating.setCreatedAt(LocalDateTime.now());

        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);
        Rating result = ratingService.createRating(ratingRequest, user);
        assertNotNull(result);
        assertEquals(ratingRequest.getRating(), result.getRating());
        assertEquals(product.getId(), result.getProduct().getId());
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    public void testCreateRating_ProductNotFound() throws ProductException {
        when(productService.findProductById(ratingRequest.getProductId())).thenThrow(new ProductException("Product not found"));
        ProductException exception = assertThrows(ProductException.class, () -> ratingService.createRating(ratingRequest, user));
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testGetProductsRating_Success() {
        Rating rating1 = new Rating();
        rating1.setId(1L);
        rating1.setRating(4.5);
        rating1.setProduct(product);
        rating1.setUser(user);
        rating1.setCreatedAt(LocalDateTime.now());

        Rating rating2 = new Rating();
        rating2.setId(2L);
        rating2.setRating(3.5);
        rating2.setProduct(product);
        rating2.setUser(user);
        rating2.setCreatedAt(LocalDateTime.now());

        List<Rating> ratings = List.of(rating1, rating2);
        when(ratingRepository.getAllProductsRating(product.getId())).thenReturn(ratings);
        List<Rating> result = ratingService.getProductsRating(product.getId());
        assertNotNull(result);
        assertEquals(2, result.size()); 
        assertEquals(4.5, result.get(0).getRating());
        assertEquals(3.5, result.get(1).getRating()); 
    }

    @Test
    public void testGetProductsRating_NoRatingsFound() {
        when(ratingRepository.getAllProductsRating(product.getId())).thenReturn(List.of());
        List<Rating> result = ratingService.getProductsRating(product.getId());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
