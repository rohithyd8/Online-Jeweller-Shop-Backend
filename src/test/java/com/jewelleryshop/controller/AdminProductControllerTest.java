package com.jewelleryshop.controller;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.request.CreateProductRequest;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminProductControllerTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private AdminProductController adminProductController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateProductHandler() throws ProductException {
		// Arrange
		CreateProductRequest createProductRequest = new CreateProductRequest();
		Product createdProduct = new Product();
		when(productService.createProduct(createProductRequest)).thenReturn(createdProduct);

		// Act
		ResponseEntity<Product> response = adminProductController.createProductHandler(createProductRequest);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(createdProduct, response.getBody());
		verify(productService, times(1)).createProduct(createProductRequest);
	}

	@Test
	void testDeleteProductHandler() throws ProductException {
		// Arrange
		Long productId = 1L;
		String expectedMessage = "Product deleted successfully";
		when(productService.deleteProduct(productId)).thenReturn(expectedMessage);

		// Act
		ResponseEntity<ApiResponse> response = adminProductController.deleteProductHandler(productId);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(expectedMessage, response.getBody().getMessage());
		verify(productService, times(1)).deleteProduct(productId);
	}

	@Test
	void testFindAllProduct() {
		// Arrange
		List<Product> products = Collections.singletonList(new Product());
		when(productService.getAllProducts()).thenReturn(products);

		// Act
		ResponseEntity<List<Product>> response = adminProductController.findAllProduct();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(products, response.getBody());
		verify(productService, times(1)).getAllProducts();
	}

	@Test
	void testRecentlyAddedProduct() {
		// Arrange
		List<Product> products = Collections.singletonList(new Product());
		when(productService.recentlyAddedProduct()).thenReturn(products);

		// Act
		ResponseEntity<List<Product>> response = adminProductController.recentlyAddedProduct();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(products, response.getBody());
		verify(productService, times(1)).recentlyAddedProduct();
	}

	@Test
	void testUpdateProductHandler() throws ProductException {
		// Arrange
		Long productId = 1L;
		Product updatedProductRequest = new Product();
		Product updatedProduct = new Product();
		when(productService.updateProduct(productId, updatedProductRequest)).thenReturn(updatedProduct);

		// Act
		ResponseEntity<Product> response = adminProductController.updateProductHandler(updatedProductRequest,
				productId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(updatedProduct, response.getBody());
		verify(productService, times(1)).updateProduct(productId, updatedProductRequest);
	}

	@Test
	void testCreateMultipleProduct() throws ProductException {
		// Arrange
		CreateProductRequest[] createProductRequests = new CreateProductRequest[] { new CreateProductRequest(),
				new CreateProductRequest() };
		ApiResponse expectedResponse = new ApiResponse("products created successfully", true);

		// Act
		ResponseEntity<ApiResponse> response = adminProductController.createMultipleProduct(createProductRequests);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
		verify(productService, times(2)).createProduct(any(CreateProductRequest.class)); // Verify that createProduct is
																							// called twice
	}
}
