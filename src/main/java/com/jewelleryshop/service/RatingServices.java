package com.jewelleryshop.service;

import java.util.List;

import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.modal.Rating;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.RatingRequest;

public interface RatingServices {
	
	public Rating createRating(RatingRequest req,User user) throws ProductException;
	
	public List<Rating> getProductsRating(Long productId);

}
