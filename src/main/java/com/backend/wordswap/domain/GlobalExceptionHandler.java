package com.backend.wordswap.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.backend.wordswap.friendshipRequest.exception.FriendshipAlreadySendedException;
import com.backend.wordswap.user.exception.UserEmailAlreadyExistsException;
import com.backend.wordswap.user.exception.UserNotFoundException;
import com.backend.wordswap.user.exception.UsernameAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(FriendshipAlreadySendedException.class)
	public ResponseEntity<String> handleFriendshipAlreadySendedException(FriendshipAlreadySendedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UsernameAlreadyExistsException.class)
	public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UserEmailAlreadyExistsException.class)
	public ResponseEntity<String> handleUserEmailAlreadyExistsException(UserEmailAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}
