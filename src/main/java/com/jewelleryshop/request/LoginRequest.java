package com.jewelleryshop.request;

import org.springframework.beans.factory.annotation.Autowired;

public class LoginRequest {
	
	@Autowired
	private String email;
	@Autowired
	private String password;
	
	
	public LoginRequest() {
		// TODO Auto-generated constructor stub
	}


	public LoginRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
