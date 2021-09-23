package com.revature.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.revature.errorhandling.ApiError;
import com.revature.errorhandling.ApiValidationError;

// Tell spring that this Advice is going to intercept all http requests that hit our controller

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	
	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		
		return ResponseEntity.status(apiError.getStatus()).body(apiError);
	}
	
	/**
	 * Intercept exceptions caused by Jhibernate validation
	 * 
	 * what happens if a user uses a post request to send an invalid user object
	 * which defies some validation that we set up in the model?
	 */
	
	// this is designed to capture any exception that might occur when a controller method takes in a "bad" object
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		String error = "Request failed validation";
		
		// instantiate an apiError object
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, error, ex);
		
		// next we can find out excactly what went wrong. 
		
		// 1. Capture the MethodArgumentNotValidException, and iterate over all the field errors
		for (FieldError e : ex.getFieldErrors()) {
			
			apiError.addSubError(new ApiValidationError(e.getObjectName(), e.getDefaultMessage(), e.getField(), e.getRejectedValue()));
		}
		
		return buildResponseEntity(apiError);
	}
	
	
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		String error = "Malformed JSON request!";
		
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
