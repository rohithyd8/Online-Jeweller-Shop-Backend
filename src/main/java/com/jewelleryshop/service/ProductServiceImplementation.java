package com.jewelleryshop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Category;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.repository.CategoryRepository;
import com.jewelleryshop.repository.ProductRepository;
import com.jewelleryshop.request.CreateProductRequest;
import com.jewelleryshop.user.domain.ProductSubCategory;

@Service
public class ProductServiceImplementation implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImplementation.class);

    private ProductRepository productRepository;
    private UserService userService;
    private CategoryRepository categoryRepository;

    public ProductServiceImplementation(ProductRepository productRepository, UserService userService,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(CreateProductRequest req) {
        logger.info("Creating product: {}", req.getTitle());

        Category topLevel = categoryRepository.findByName(req.getTopLavelCategory());
        if (topLevel == null) {
            logger.info("Top level category '{}' not found, creating new one.", req.getTopLavelCategory());
            Category topLavelCategory = new Category();
            topLavelCategory.setName(req.getTopLavelCategory());
            topLavelCategory.setLevel(1);
            topLevel = categoryRepository.save(topLavelCategory);
        }

        Category secondLevel = categoryRepository.findByNameAndParant(req.getSecondLavelCategory(), topLevel.getName());
        if (secondLevel == null) {
            logger.info("Second level category '{}' not found, creating new one.", req.getSecondLavelCategory());
            Category secondLavelCategory = new Category();
            secondLavelCategory.setName(req.getSecondLavelCategory());
            secondLavelCategory.setParentCategory(topLevel);
            secondLavelCategory.setLevel(2);
            secondLevel = categoryRepository.save(secondLavelCategory);
        }

        Category thirdLevel = categoryRepository.findByNameAndParant(req.getThirdLavelCategory(), secondLevel.getName());
        if (thirdLevel == null) {
            logger.info("Third level category '{}' not found, creating new one.", req.getThirdLavelCategory());
            Category thirdLavelCategory = new Category();
            thirdLavelCategory.setName(req.getThirdLavelCategory());
            thirdLavelCategory.setParentCategory(secondLevel);
            thirdLavelCategory.setLevel(3);
            thirdLevel = categoryRepository.save(thirdLavelCategory);
        }

        Product product = new Product();
        product.setTitle(req.getTitle());
        product.setColor(req.getColor());
        product.setDescription(req.getDescription());
        product.setDiscountedPrice(req.getDiscountedPrice());
        product.setDiscountPersent(req.getDiscountPersent());
        product.setImageUrl(req.getImageUrl());
        product.setBrand(req.getBrand());
        product.setPrice(req.getPrice());
        product.setSizes(req.getSize());
        product.setQuantity(req.getQuantity());
        product.setCategory(thirdLevel);
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        logger.info("Product created successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    public String deleteProduct(Long productId) throws ProductException {
        logger.info("Attempting to delete product with ID: {}", productId);

        Product product = findProductById(productId);
        logger.info("Deleting product: {}", product.getId());

        product.getSizes().clear(); // Clearing associated sizes
        productRepository.delete(product);

        logger.info("Product deleted successfully with ID: {}", productId);
        return "Product deleted successfully";
    }

    @Override
    public Product updateProduct(Long productId, Product req) throws ProductException {
        logger.info("Updating product with ID: {}", productId);

        Product product = findProductById(productId);

        if (req.getQuantity() != 0) {
            product.setQuantity(req.getQuantity());
            logger.info("Updated quantity for product with ID: {} to {}", productId, req.getQuantity());
        }
        if (req.getDescription() != null) {
            product.setDescription(req.getDescription());
            logger.info("Updated description for product with ID: {}", productId);
        }

        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully with ID: {}", productId);
        return updatedProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productRepository.findAll();
    }

    @Override
    public Product findProductById(Long id) throws ProductException {
        logger.info("Fetching product with ID: {}", id);

        Optional<Product> opt = productRepository.findById(id);
        if (opt.isPresent()) {
            logger.info("Product found with ID: {}", id);
            return opt.get();
        }

        logger.error("Product not found with ID: {}", id);
        throw new ProductException("Product not found with id " + id);
    }

    @Override
    public List<Product> findProductByCategory(String category) {
        logger.info("Fetching products by category: {}", category);
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> searchProduct(String query) {
        logger.info("Searching products with query: {}", query);
        return productRepository.searchProduct(query);
    }

    @Override
    public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice,
            Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {

        logger.info("Fetching products with filters - Category: {}, Colors: {}, Sizes: {}, Min Price: {}, Max Price: {}, Min Discount: {}, Stock: {}, Page: {} of size {}", 
            category, colors, sizes, minPrice, maxPrice, minDiscount, stock, pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Product> products = productRepository.filterProducts(category, minPrice, maxPrice, minDiscount, sort);

        if (!colors.isEmpty()) {
            products = products.stream().filter(p -> colors.stream().anyMatch(c -> c.equalsIgnoreCase(p.getColor())))
                    .collect(Collectors.toList());
        }

        if (stock != null) {
            if (stock.equals("in_stock")) {
                products = products.stream().filter(p -> p.getQuantity() > 0).collect(Collectors.toList());
            } else if (stock.equals("out_of_stock")) {
                products = products.stream().filter(p -> p.getQuantity() < 1).collect(Collectors.toList());
            }
        }

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        List<Product> pageContent = products.subList(startIndex, endIndex);
        return new PageImpl<>(pageContent, pageable, products.size());
    }

    @Override
    public List<Product> recentlyAddedProduct() {
        logger.info("Fetching recently added products.");
        return productRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Product> getRelatedProducts(String topLavelCategory, String secondLavelCategory,
            String thirdLavelCategory) {
        logger.info("Fetching related products for - Top Level: {}, Second Level: {}, Third Level: {}",
            topLavelCategory, secondLavelCategory, thirdLavelCategory);
        return productRepository.findRelatedProducts(topLavelCategory, secondLavelCategory, thirdLavelCategory);
    }
}
