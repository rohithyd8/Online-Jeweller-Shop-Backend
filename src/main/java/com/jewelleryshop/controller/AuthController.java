package com.jewelleryshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewelleryshop.config.JwtTokenProvider;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.UserRepository;
import com.jewelleryshop.request.LoginRequest;
import com.jewelleryshop.response.AuthResponse;
import com.jewelleryshop.service.CartService;
import com.jewelleryshop.service.CustomUserDetails;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Create a logger instance for the class
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetails customUserDetails;
    private CartService cartService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, CustomUserDetails customUserDetails, CartService cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetails = customUserDetails;
        this.cartService = cartService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody User user) throws UserException {
        String email = user.getEmail();
        String password = user.getPassword();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String role = user.getRole();

        logger.info("Received signup request for user with email: {}", email);

        User isEmailExist = userRepository.findByEmail(email);

        // Check if user with the given email already exists
        if (isEmailExist != null) {
            logger.error("Email {} is already in use.", email);
            throw new UserException("Email Is Already Used With Another Account");
        }

        // Create new user
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFirstName(firstName);
        createdUser.setLastName(lastName);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setRole(role);

        User savedUser = userRepository.save(createdUser);
        logger.info("User created successfully with email: {}", savedUser.getEmail());

        cartService.createCart(savedUser);
        logger.info("Cart created for user with email: {}", savedUser.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, true);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        logger.info("Received signin request for username: {}", username);

        try {
            Authentication authentication = authenticate(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setStatus(true);
            authResponse.setJwt(token);

            logger.info("Signin successful for user: {}", username);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            logger.error("Invalid credentials for user: {}", username);
            throw ex;  // Rethrow the exception to propagate it
        }
    }

    private Authentication authenticate(String username, String password) {
        logger.debug("Authenticating user with username: {}", username);

        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        if (userDetails == null) {
            logger.error("User not found with username: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            logger.error("Password mismatch for user: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        logger.debug("Authentication successful for user: {}", username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
