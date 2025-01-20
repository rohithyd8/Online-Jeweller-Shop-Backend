package com.jewelleryshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.AddItemRequest;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.service.CartService;
import com.jewelleryshop.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    // Logger instance for the class
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private CartService cartService;
    private UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<Cart> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws UserException {

        logger.info("Received request to find user cart using JWT: {}", jwt);

        User user = userService.findUserProfileByJwt(jwt);
        logger.info("User found: {}", user.getEmail());

        Cart cart = cartService.findUserCart(user.getId());
        if (cart != null) {
            logger.info("Cart found for user with email: {}", cart.getUser().getEmail());
        } else {
            logger.warn("No cart found for user with email: {}", user.getEmail());
        }

        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody AddItemRequest req, 
                                                  @RequestHeader("Authorization") String jwt) 
            throws UserException, ProductException {

        logger.info("Received request to add item to cart for user with JWT: {}", jwt);

        User user = userService.findUserProfileByJwt(jwt);
        logger.info("User found: {}", user.getEmail());

        CartItem item = cartService.addCartItem(user.getId(), req);
        logger.info("Item added to cart: {} for user with email: {}", item.getProduct().getTitle(), user.getEmail());

        ApiResponse res = new ApiResponse("Item Added To Cart Successfully", true);
        return new ResponseEntity<>(item, HttpStatus.ACCEPTED);
    }
}
