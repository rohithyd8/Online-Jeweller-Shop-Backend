package com.jewelleryshop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.CartRepository;
import com.jewelleryshop.request.AddItemRequest;

@Service
public class CartServiceImplementation implements CartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImplementation.class);

    private CartRepository cartRepository;
    private CartItemService cartItemService;
    private ProductService productService;
    
    public CartServiceImplementation(CartRepository cartRepository, CartItemService cartItemService,
            ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cartItemService = cartItemService;
    }

    @Override
    public Cart createCart(User user) {
        logger.info("Creating new cart for user with ID: {}", user.getId());
        
        Cart cart = new Cart();
        cart.setUser(user);
        Cart createdCart = cartRepository.save(cart);
        
        logger.info("Created new cart with ID: {}", createdCart.getId());
        return createdCart;
    }

    public Cart findUserCart(Long userId) {
        logger.info("Fetching cart for user with ID: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId);
        
        if (cart != null) {
            int totalPrice = 0;
            int totalDiscountedPrice = 0;
            int totalItem = 0;
            
            for (CartItem cartItem : cart.getCartItems()) {
                totalPrice += cartItem.getPrice();
                totalDiscountedPrice += cartItem.getDiscountedPrice();
                totalItem += cartItem.getQuantity();
            }
            
            cart.setTotalPrice(totalPrice);
            cart.setTotalDiscountedPrice(totalDiscountedPrice);
            cart.setDiscounte(totalPrice - totalDiscountedPrice);
            cart.setTotalItem(totalItem);
            
            logger.info("Cart details updated for user with ID: {}. Total Price: {}, Total Discounted Price: {}, Total Items: {}", 
                        userId, totalPrice, totalDiscountedPrice, totalItem);
            
            return cartRepository.save(cart);
        }
        
        logger.warn("Cart not found for user with ID: {}", userId);
        return null;  // Or you can throw an exception depending on your use case
    }

    @Override
    public CartItem addCartItem(Long userId, AddItemRequest req) throws ProductException {
        logger.info("Adding product with ID: {} to cart for user with ID: {}", req.getProductId(), userId);
        
        Cart cart = cartRepository.findByUserId(userId);
        Product product = productService.findProductById(req.getProductId());
        
        if (cart == null || product == null) {
            logger.error("Cart or product not found for user ID: {} and product ID: {}", userId, req.getProductId());
            throw new ProductException("Product or Cart not found");
        }
        
        CartItem existingCartItem = cartItemService.isCartItemExist(cart, product, req.getSize(), userId);
        
        if (existingCartItem == null) {
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(req.getQuantity());
            newCartItem.setUserId(userId);
            newCartItem.setSize(req.getSize());
            
            int price = req.getQuantity() * product.getDiscountedPrice();
            newCartItem.setPrice(price);
            
            CartItem createdCartItem = cartItemService.createCartItem(newCartItem);
            cart.getCartItems().add(createdCartItem);
            
            logger.info("Successfully added new CartItem with ID: {} for user ID: {}", createdCartItem.getId(), userId);
            return createdCartItem;
        }
        
        logger.info("CartItem already exists for product ID: {} and size: {}. Returning existing CartItem ID: {}", 
                    req.getProductId(), req.getSize(), existingCartItem.getId());
        
        return existingCartItem;
    }
}
