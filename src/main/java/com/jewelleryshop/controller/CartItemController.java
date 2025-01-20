package com.jewelleryshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.CartItemException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.service.CartItemService;
import com.jewelleryshop.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/cart_items")
@Tag(name="Cart Item Management", description = "create cart item delete cart item")
public class CartItemController {

    private static final Logger logger = LoggerFactory.getLogger(CartItemController.class);

    private CartItemService cartItemService;
    private UserService userService;
    
    public CartItemController(CartItemService cartItemService, UserService userService) {
        this.cartItemService = cartItemService;
        this.userService = userService;
    }
    
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItemHandler(@PathVariable Long cartItemId, 
                                                              @RequestHeader("Authorization") String jwt) 
            throws CartItemException, UserException {
        
        logger.info("Received request to delete cart item with ID: {} for user with JWT: {}", cartItemId, jwt);
        
        // Find the user based on JWT
        User user = userService.findUserProfileByJwt(jwt);
        logger.info("User found: {}", user.getEmail());
        
        // Remove cart item
        cartItemService.removeCartItem(user.getId(), cartItemId);
        logger.info("Item with ID: {} successfully removed from cart for user: {}", cartItemId, user.getEmail());

        ApiResponse res = new ApiResponse("Item Removed From Cart", true);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }
    
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemHandler(@PathVariable Long cartItemId, 
                                                          @RequestBody CartItem cartItem, 
                                                          @RequestHeader("Authorization") String jwt) 
            throws CartItemException, UserException {
        
        logger.info("Received request to update cart item with ID: {} for user with JWT: {}", cartItemId, jwt);
        
        // Find the user based on JWT
        User user = userService.findUserProfileByJwt(jwt);
        logger.info("User found: {}", user.getEmail());
        
        // Update cart item
        CartItem updatedCartItem = cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);
        logger.info("Cart item with ID: {} successfully updated for user: {}", cartItemId, user.getEmail());

        return new ResponseEntity<>(updatedCartItem, HttpStatus.ACCEPTED);
    }
}
