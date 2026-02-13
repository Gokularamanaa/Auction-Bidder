package com.springboot.AuctionBidder.Service;

import com.springboot.AuctionBidder.Entity.User;
import com.springboot.AuctionBidder.dto.RegisterRequest;

public interface AuthService {
	User register(RegisterRequest request);
}
