package com.jewelleryshop.controller;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserProductControllerTest {

	@InjectMocks
	private UserProductController userProductController;

	@Mock
	private ProductService productService;

	private Product product;
	private List<Product> productList;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Create a mock product for testing
		product = new Product();
		product.setId(1L);
		product.setTitle("Gold Ring");
		product.setPrice(1000);
		product.setDiscountedPrice(900);
		product.setDiscountPersent(10);
		product.setQuantity(5);
		product.setBrand("BrandX");
		product.setColor("Gold");

		// Create a list of mock products
		productList = Arrays.asList(product);
	}

	@Test
	void testFindProductByCategoryHandler() {
		// Arrange
		String category = "jewelry";
		List<String> color = Collections.singletonList("gold");
		List<String> size = Collections.singletonList("M");
		Integer minPrice = 500;
		Integer maxPrice = 2000;
		Integer minDiscount = 10;
		String sort = "price";
		String stock = "inStock";
		Integer pageNumber = 1;
		Integer pageSize = 10;

		Page<Product> productPage = mock(Page.class);
		when(productService.getAllProduct(eq(category), eq(color), eq(size), eq(minPrice), eq(maxPrice),
				eq(minDiscount), eq(sort), eq(stock), eq(pageNumber), eq(pageSize))).thenReturn(productPage);

		// Act
		ResponseEntity<Page<Product>> response = userProductController.findProductByCategoryHandler(category, color,
				size, minPrice, maxPrice, minDiscount, sort, stock, pageNumber, pageSize);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(productPage, response.getBody());
		verify(productService, times(1)).getAllProduct(eq(category), eq(color), eq(size), eq(minPrice), eq(maxPrice),
				eq(minDiscount), eq(sort), eq(stock), eq(pageNumber), eq(pageSize));
	}

	@Test
	void testFindProductByIdHandler() throws ProductException {
		// Arrange
		Long productId = 1L;
		when(productService.findProductById(eq(productId))).thenReturn(product);

		// Act
		ResponseEntity<Product> response = userProductController.findProductByIdHandler(productId);

		// Assert
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals(product, response.getBody());
		verify(productService, times(1)).findProductById(eq(productId));
	}

	@Test
	void testSearchProductHandler() {
		// Arrange
		String query = "ring";
		when(productService.searchProduct(eq(query))).thenReturn(productList);

		// Act
		ResponseEntity<List<Product>> response = userProductController.searchProductHandler(query);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(productList, response.getBody());
		verify(productService, times(1)).searchProduct(eq(query));
	}

	@Test
	void testGetRelatedProductsHandler() {
		// Arrange
		String topLavelCategory = "jewelry";
		String secondLavelCategory = "rings";
		String thirdLavelCategory = "gold";
		when(productService.getRelatedProducts(eq(topLavelCategory), eq(secondLavelCategory), eq(thirdLavelCategory)))
				.thenReturn(productList);

		// Act
		ResponseEntity<List<Product>> response = userProductController.getRelatedProductsHandler(topLavelCategory,
				secondLavelCategory, thirdLavelCategory);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(productList, response.getBody());
		verify(productService, times(1)).getRelatedProducts(eq(topLavelCategory), eq(secondLavelCategory),
				eq(thirdLavelCategory));
	}

	@Test
	void testGetRelatedProductsHandler_EmptyList() {
		// Arrange
		String topLavelCategory = "jewelry";
		String secondLavelCategory = "rings";
		String thirdLavelCategory = "gold";
		when(productService.getRelatedProducts(eq(topLavelCategory), eq(secondLavelCategory), eq(thirdLavelCategory)))
				.thenReturn(Collections.emptyList());

		// Act
		ResponseEntity<List<Product>> response = userProductController.getRelatedProductsHandler(topLavelCategory,
				secondLavelCategory, thirdLavelCategory);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().isEmpty());
		verify(productService, times(1)).getRelatedProducts(eq(topLavelCategory), eq(secondLavelCategory),
				eq(thirdLavelCategory));
	}

	@Test
	void testGetRelatedProductsHandler_BadRequest() {
		String topLavelCategory = "";
		String secondLavelCategory = "jewellery";
		String thirdLavelCategory = "Giva";

		// Act
		ResponseEntity<List<Product>> response = userProductController.getRelatedProductsHandler(topLavelCategory,
				secondLavelCategory, thirdLavelCategory);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		verify(productService, times(0)).getRelatedProducts(anyString(), anyString(), anyString());
	}
}
