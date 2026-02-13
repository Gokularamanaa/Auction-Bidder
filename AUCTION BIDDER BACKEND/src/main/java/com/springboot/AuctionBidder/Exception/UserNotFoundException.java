package com.springboot.AuctionBidder.Exception;

import java.io.Serial;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends RuntimeException{

	@Serial
    private static final long serialVersionUID = 1L;
	private final HttpStatus status;

	public HttpStatus getStatus() {
		return status;
	}

	public UserNotFoundException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
