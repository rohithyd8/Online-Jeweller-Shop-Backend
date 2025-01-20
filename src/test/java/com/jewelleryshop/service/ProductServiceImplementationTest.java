package com.jewelleryshop.service;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Category;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.Size;
import com.jewelleryshop.request.CreateProductRequest;
import com.jewelleryshop.repository.CategoryRepository;
import com.jewelleryshop.repository.ProductRepository;
import com.jewelleryshop.service.ProductServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceImplementationTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImplementation productService;

    private CreateProductRequest createProductRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize request data without hardcoded values
        createProductRequest = new CreateProductRequest();
        createProductRequest.setTitle("Product Title" + UUID.randomUUID().toString().substring(0, 5));  // Dynamic title
        createProductRequest.setPrice(new Random().nextInt(1000) + 100);  // Random price between 100 and 1000
        createProductRequest.setDiscountedPrice(createProductRequest.getPrice() - 100);  // Discounted price
        createProductRequest.setDiscountPersent(10);
        createProductRequest.setQuantity(10);
        createProductRequest.setBrand("Brand " + UUID.randomUUID().toString().substring(0, 5)); // Random brand
        createProductRequest.setColor("Color " + UUID.randomUUID().toString().substring(0, 3));  // Random color
        createProductRequest.setImageUrl("http://example.com/" + UUID.randomUUID().toString() + ".jpg");  // Dynamic URL
        createProductRequest.setTopLavelCategory("Jewelry");
        createProductRequest.setSecondLavelCategory("Necklaces");
        createProductRequest.setThirdLavelCategory("Gold Necklaces");
        createProductRequest.setSize(new HashSet<>(Arrays.asList(new Size(), new Size())));  // Dynamic size
    }

   /* @Test
    public void testCreateProduct_Success() throws ProductException {
        // Mocking category creation
        Category topLevelCategory = new Category();
        topLevelCategory.setName(createProductRequest.getTopLavelCategory());
        when(categoryRepository.findByName(createProductRequest.getTopLavelCategory())).thenReturn(topLevelCategory);

        Category secondLevelCategory = new Category();
        secondLevelCategory.setName(createProductRequest.getSecondLavelCategory());
        when(categoryRepository.findByNameAndParant(createProductRequest.getSecondLavelCategory(), createProductRequest.getTopLavelCategory())).thenReturn(secondLevelCategory);

        Category thirdLevelCategory = new Category();
        thirdLevelCategory.setName(createProductRequest.getThirdLavelCategory());
        when(categoryRepository.findByNameAndParant(createProductRequest.getThirdLavelCategory(), createProductRequest.getSecondLavelCategory())).thenReturn(thirdLevelCategory);

        // Mock product saving
        Product savedProduct = new Product();
        savedProduct.setTitle(createProductRequest.getTitle());
        savedProduct.setPrice(createProductRequest.getPrice());
        savedProduct.setDiscountedPrice(createProductRequest.getDiscountedPrice());
        savedProduct.setDiscountPersent(createProductRequest.getDiscountPersent());
        savedProduct.setQuantity(createProductRequest.getQuantity());
        savedProduct.setBrand(createProductRequest.getBrand());
        savedProduct.setColor(createProductRequest.getColor());
        savedProduct.setImageUrl(createProductRequest.getImageUrl());
        savedProduct.setCategory(thirdLevelCategory);
        savedProduct.setCreatedAt(LocalDateTime.now());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Call the service method
        Product createdProduct = productService.createProduct(createProductRequest);

        // Assertions
        assertNotNull(createdProduct);
        assertEquals(createProductRequest.getTitle(), createdProduct.getTitle());
        assertEquals(createProductRequest.getPrice(), createdProduct.getPrice());
        assertEquals(createProductRequest.getDiscountedPrice(), createdProduct.getDiscountedPrice());
        assertEquals(createProductRequest.getQuantity(), createdProduct.getQuantity());
        assertEquals(createProductRequest.getBrand(), createdProduct.getBrand());
        assertEquals(createProductRequest.getColor(), createdProduct.getColor());
        assertEquals(createProductRequest.getImageUrl(), createdProduct.getImageUrl());
        assertEquals(createProductRequest.getThirdLavelCategory(), createdProduct.getCategory().getName());
    }*/

   /* @Test
    public void testCreateProduct_CategoryNotFound() {
        // Simulate missing top-level category
        when(categoryRepository.findByName(createProductRequest.getTopLavelCategory())).thenReturn(null);

        // Act & Assert
        assertThrows(ProductException.class, () -> {
            productService.createProduct(createProductRequest);
        });

        // Verify that no product is saved
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void testCreateProduct_SecondLevelCategoryNotFound() throws ProductException {
        // Mock top-level category
        Category topLevelCategory = new Category();
        topLevelCategory.setName(createProductRequest.getTopLavelCategory());
        when(categoryRepository.findByName(createProductRequest.getTopLavelCategory())).thenReturn(topLevelCategory);

        // Simulate missing second-level category
        when(categoryRepository.findByNameAndParant(createProductRequest.getSecondLavelCategory(), createProductRequest.getTopLavelCategory())).thenReturn(null);

        // Act & Assert
        assertThrows(ProductException.class, () -> {
            productService.createProduct(createProductRequest);
        });

        // Verify that no product is saved
        verify(productRepository, times(0)).save(any(Product.class));
    }*/

   /* @Test
    public void testCreateProduct_ThirdLevelCategoryNotFound() throws ProductException {
        // Mock top-level and second-level categories
        Category topLevelCategory = new Category();
        topLevelCategory.setName(createProductRequest.getTopLavelCategory());
        when(categoryRepository.findByName(createProductRequest.getTopLavelCategory())).thenReturn(topLevelCategory);

        Category secondLevelCategory = new Category();
        secondLevelCategory.setName(createProductRequest.getSecondLavelCategory());
        when(categoryRepository.findByNameAndParant(createProductRequest.getSecondLavelCategory(), createProductRequest.getTopLavelCategory())).thenReturn(secondLevelCategory);

        // Simulate missing third-level category
        when(categoryRepository.findByNameAndParant(createProductRequest.getThirdLavelCategory(), createProductRequest.getSecondLavelCategory())).thenReturn(null);

        // Act & Assert
        assertThrows(ProductException.class, () -> {
            productService.createProduct(createProductRequest);
        });

        // Verify that no product is saved
        verify(productRepository, times(0)).save(any(Product.class));
    }*/

    @Test
    public void testDeleteProduct_Success() throws ProductException {
        // Mock product and category behavior
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(50);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        String result = productService.deleteProduct(1L);

        // Assert
        assertEquals("Product deleted Successfully", result);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    public void testDeleteProduct_ProductNotFound() {
        // Simulate product not found scenario
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductException.class, () -> {
            productService.deleteProduct(1L);
        });

        verify(productRepository, times(0)).delete(any(Product.class));
    }

   /* @Test
    public void testUpdateProduct_Success() throws ProductException {
        // Mock existing product and update behavior
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setQuantity(50);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        Product updatedProduct = new Product();
        updatedProduct.setQuantity(30);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertEquals(30, result.getQuantity());
        verify(productRepository, times(1)).save(result);
    }*/

    @Test
    public void testUpdateProduct_ProductNotFound() {
        // Simulate product not found scenario
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductException.class, () -> {
            productService.updateProduct(1L, new Product());
        });

        verify(productRepository, times(0)).save(any(Product.class));
    }
}
