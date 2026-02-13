package com.springboot.AuctionBidder.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.springboot.AuctionBidder.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 400 Bad Request for InvalidEmailException
	@ExceptionHandler(InvalidEmailException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidEmail(InvalidEmailException ex, HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), // 400
				"Bad Request", ex.getMessage(), // "Invalid email: xyz@"
				request.getRequestURI());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidBidException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidBid(InvalidBidException ex, HttpServletRequest request) {
		ApiErrorResponse error = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
				"Bad Request", ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	// 404 ResourceNotFoundException
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

		ApiErrorResponse error = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
				request.getRequestURI());

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	// 403 AccessDeniedException (Security)
	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(
			org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
		ApiErrorResponse error = new ApiErrorResponse(
				HttpStatus.FORBIDDEN.value(),
				"Forbidden",
				"Access Denied: You do not have permission to access this resource",
				request.getRequestURI());
		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}

	// 409 Conflict - Optimistic Locking Failure
	@ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<ApiErrorResponse> handleOptimisticLockingFailure(
			org.springframework.orm.ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
		ApiErrorResponse error = new ApiErrorResponse(
				HttpStatus.CONFLICT.value(),
				"Conflict",
				"This auction has been updated by another user. Please retry your bid.",
				request.getRequestURI());
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	// Generic Exception Handler for unexpected security issues
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
		ApiErrorResponse error = new ApiErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				ex.getMessage(),
				request.getRequestURI());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}