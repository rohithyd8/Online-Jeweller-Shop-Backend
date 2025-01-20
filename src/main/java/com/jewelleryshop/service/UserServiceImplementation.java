package com.jewelleryshop.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jewelleryshop.config.JwtTokenProvider;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplementation.class);
    
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    
    public UserServiceImplementation(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User findUserById(Long userId) throws UserException {
        logger.info("Attempting to find user with ID: {}", userId);
        
        Optional<User> user = userRepository.findById(userId);
        
        if (user.isPresent()) {
            logger.info("User found with ID: {}", userId);
            return user.get();
        }
        
        logger.error("User not found with ID: {}", userId);
        throw new UserException("User not found with ID " + userId);
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        logger.info("Extracting user profile from JWT token");
        
        String email = jwtTokenProvider.getEmailFromJwtToken(jwt);
        logger.info("Email extracted from JWT token: {}", email);
        
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UserException("User not exist with email " + email);
        }
        
        logger.info("User found with email: {}", email);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        logger.info("Fetching all users ordered by creation date.");
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        logger.info("Fetched {} users", users.size());
        return users;
    }

    public User createUser(User user) {
        logger.info("Attempting to create a new user: {}", user);
        // You can add creation logic here
        return null;
    }

    public User findUserByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        // You can add logic to find a user by email here
        return null;
    }

    public User updateUser(long userId, User user) {
        logger.info("Attempting to update user with ID: {}", userId);
        // You can add user update logic here
        return null;
    }
}
