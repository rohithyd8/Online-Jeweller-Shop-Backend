package com.jewelleryshop.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.service.ProductService;
import com.jewelleryshop.user.domain.ProductSubCategory;

@RestController
@RequestMapping("/api")
public class UserProductController {

    private static final Logger logger = LoggerFactory.getLogger(UserProductController.class);

    private ProductService productService;

    public UserProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> findProductByCategoryHandler(@RequestParam String category,
            @RequestParam List<String> color, @RequestParam List<String> size, @RequestParam Integer minPrice,
            @RequestParam Integer maxPrice, @RequestParam Integer minDiscount, @RequestParam String sort,
            @RequestParam String stock, @RequestParam Integer pageNumber, @RequestParam Integer pageSize) {

        // Log incoming request
        logger.info("Received request to fetch products with category: {}, color: {}, size: {}, minPrice: {}, maxPrice: {}, minDiscount: {}, sort: {}, stock: {}, pageNumber: {}, pageSize: {}",
                category, color, size, minPrice, maxPrice, minDiscount, sort, stock, pageNumber, pageSize);

        Page<Product> res = productService.getAllProduct(category, color, size, minPrice, maxPrice, minDiscount, sort,
                stock, pageNumber, pageSize);

        // Log the successful retrieval of products
        logger.info("Successfully retrieved products for category: {}", category);

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @GetMapping("/products/id/{productId}")
    public ResponseEntity<Product> findProductByIdHandler(@PathVariable Long productId) throws ProductException {

        // Log request for product by ID
        logger.info("Received request to fetch product with ID: {}", productId);

        Product product = productService.findProductById(productId);

        // Log the successful retrieval of product by ID
        logger.info("Successfully retrieved product with ID: {}", productId);

        return new ResponseEntity<>(product, HttpStatus.ACCEPTED);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProductHandler(@RequestParam String q) {

        // Log search query
        logger.info("Received search request with query: {}", q);

        List<Product> products = productService.searchProduct(q);

        // Log the number of products found
        logger.info("Found {} products for search query: {}", products.size(), q);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products/related")
    public ResponseEntity<List<Product>> getRelatedProductsHandler(@RequestParam String topLavelCategory,
            @RequestParam String secondLavelCategory, @RequestParam String thirdLavelCategory) {

        // Validate parameters and log the request
        logger.info("Received request to fetch related products for categories: topLevel: {}, secondLevel: {}, thirdLevel: {}",
                topLavelCategory, secondLavelCategory, thirdLavelCategory);

        // Check for invalid parameters
        if (topLavelCategory.isEmpty() || secondLavelCategory.isEmpty() || thirdLavelCategory.isEmpty()) {
            logger.error("Invalid parameters, one or more categories are empty.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 if any parameter is missing
        }

        // Fetch related products
        List<Product> relatedProducts = productService.getRelatedProducts(topLavelCategory, secondLavelCategory,
                thirdLavelCategory);

        // If no related products are found, log it and return empty list
        if (relatedProducts.isEmpty()) {
            logger.warn("No related products found for categories: topLevel: {}, secondLevel: {}, thirdLevel: {}",
                    topLavelCategory, secondLavelCategory, thirdLavelCategory);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK); // Return empty array with 200 OK
        }

        // Log successful retrieval of related products
        logger.info("Successfully retrieved {} related products for categories: topLevel: {}, secondLevel: {}, thirdLevel: {}",
                relatedProducts.size(), topLavelCategory, secondLavelCategory, thirdLavelCategory);

        return new ResponseEntity<>(relatedProducts, HttpStatus.OK); // Return 200 with the products
    }
}
