package com.jewelleryshop.service;

import java.util.List;

import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.User;

public interface UserService {
	
	public User findUserById(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public List<User> findAllUsers();

}
