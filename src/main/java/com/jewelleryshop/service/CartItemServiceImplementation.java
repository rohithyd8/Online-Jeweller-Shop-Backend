package com.jewelleryshop.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jewelleryshop.exception.CartItemException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.CartItemRepository;
import com.jewelleryshop.repository.CartRepository;

@Service
public class CartItemServiceImplementation implements CartItemService {
    
    private static final Logger logger = LoggerFactory.getLogger(CartItemServiceImplementation.class);

    private CartItemRepository cartItemRepository;
    private UserService userService;
    private CartRepository cartRepository;

    public CartItemServiceImplementation(CartItemRepository cartItemRepository, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
    }

    @Override
    public CartItem createCartItem(CartItem cartItem) {
        logger.info("Creating new CartItem for product: {}", cartItem.getProduct().getTitle());
        
        cartItem.setQuantity(1);
        cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
        cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice() * cartItem.getQuantity());
        
        CartItem createdCartItem = cartItemRepository.save(cartItem);
        
        logger.info("Created CartItem with ID: {}", createdCartItem.getId());
        return createdCartItem;
    }

    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {
        logger.info("Attempting to update CartItem with ID: {}", id);
        
        CartItem item = findCartItemById(id);
        User user = userService.findUserById(item.getUserId());
        
        if (user.getId().equals(userId)) {
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(item.getQuantity() * item.getProduct().getPrice());
            item.setDiscountedPrice(item.getQuantity() * item.getProduct().getDiscountedPrice());

            CartItem updatedCartItem = cartItemRepository.save(item);
            logger.info("Successfully updated CartItem with ID: {}", updatedCartItem.getId());
            return updatedCartItem;
        } else {
            logger.warn("User with ID: {} attempted to update another user's CartItem with ID: {}", userId, id);
            throw new CartItemException("You can't update another user's cart item");
        }
    }

    @Override
    public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId) {
        logger.info("Checking if CartItem exists for user ID: {} and product ID: {}", userId, product.getId());
        
        CartItem cartItem = cartItemRepository.isCartItemExist(cart, product, size, userId);
        
        if (cartItem != null) {
            logger.info("CartItem found with ID: {}", cartItem.getId());
        } else {
            logger.info("No CartItem found for product ID: {} and user ID: {}", product.getId(), userId);
        }
        return cartItem;
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException {
        logger.info("Attempting to remove CartItem with ID: {} for user ID: {}", cartItemId, userId);
        
        CartItem cartItem = findCartItemById(cartItemId);
        User user = userService.findUserById(cartItem.getUserId());
        User reqUser = userService.findUserById(userId);
        
        if (user.getId().equals(reqUser.getId())) {
            cartItemRepository.deleteById(cartItem.getId());
            logger.info("Successfully removed CartItem with ID: {} for user ID: {}", cartItemId, userId);
        } else {
            logger.warn("User with ID: {} attempted to remove another user's CartItem with ID: {}", userId, cartItemId);
            throw new UserException("You can't remove another user's item");
        }
    }

    @Override
    public CartItem findCartItemById(Long cartItemId) throws CartItemException {
        logger.info("Fetching CartItem with ID: {}", cartItemId);
        
        Optional<CartItem> opt = cartItemRepository.findById(cartItemId);
        
        if (opt.isPresent()) {
            logger.info("Found CartItem with ID: {}", cartItemId);
            return opt.get();
        }
        
        logger.error("CartItem not found with ID: {}", cartItemId);
        throw new CartItemException("CartItem not found with ID: " + cartItemId);
    }
}
