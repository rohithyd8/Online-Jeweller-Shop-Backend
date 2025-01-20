package com.jewelleryshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jewelleryshop.config.JwtTokenProvider;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	

	@Override
	public User findUserById(Long userId) throws UserException {
		Optional<User> user=userRepository.findById(userId);
		
		if(user.isPresent()){
			return user.get();
		}
		throw new UserException("user not found with id "+userId);
	}

	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		System.out.println("user service");
		String email=jwtTokenProvider.getEmailFromJwtToken(jwt);
		
		System.out.println("email"+email);
		
		User user=userRepository.findByEmail(email);
		
		if(user==null) {
			throw new UserException("user not exist with email "+email);
		}
		System.out.println("email user"+user.getEmail());
		return user;
	}

//	@Override
//	public List<User> findAllCustomers() {
//		return null;
//	}

	@Override
	public List<User> findAllUsers() {
		// TODO Auto-generated method stub
		return userRepository.findAllByOrderByCreatedAtDesc();
	}

	public User createUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	public User findUserByEmail(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public User updateUser(long l, User user) {
		// TODO Auto-generated method stub
		return null;
	}

}
