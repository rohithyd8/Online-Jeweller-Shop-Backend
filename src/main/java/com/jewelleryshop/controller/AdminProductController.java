package com.jewelleryshop.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.request.CreateProductRequest;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.service.ProductService;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    // Create a logger instance for the class
    private static final Logger logger = LoggerFactory.getLogger(AdminProductController.class);

    private ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public ResponseEntity<Product> createProductHandler(@RequestBody CreateProductRequest req) throws ProductException {
        logger.info("Received request to create product with details: {}", req);
        Product createdProduct = productService.createProduct(req);
        logger.info("Product created successfully with ID: {}", createdProduct.getId());
        return new ResponseEntity<>(createdProduct, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProductHandler(@PathVariable Long productId) throws ProductException {
        logger.info("Received request to delete product with ID: {}", productId);
        String msg = productService.deleteProduct(productId);
        logger.info("Product with ID: {} deleted successfully. Message: {}", productId, msg);
        ApiResponse res = new ApiResponse(msg, true);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> findAllProduct() {
        logger.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        logger.info("Fetched {} products", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Product>> recentlyAddedProduct() {
        logger.info("Fetching recently added products");
        List<Product> products = productService.recentlyAddedProduct();
        logger.info("Fetched {} recently added products", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping("/{productId}/update")
    public ResponseEntity<Product> updateProductHandler(@RequestBody Product req, @PathVariable Long productId) throws ProductException {
        logger.info("Received request to update product with ID: {}", productId);
        Product updatedProduct = productService.updateProduct(productId, req);
        logger.info("Product with ID: {} updated successfully", updatedProduct.getId());
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PostMapping("/creates")
    public ResponseEntity<ApiResponse> createMultipleProduct(@RequestBody CreateProductRequest[] reqs) throws ProductException {
        logger.info("Received request to create multiple products. Total products: {}", reqs.length);
        for (CreateProductRequest product : reqs) {
            productService.createProduct(product);
        }
        logger.info("Successfully created {} products", reqs.length);
        ApiResponse res = new ApiResponse("Products created successfully", true);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }
}
