package com.backend.wordswap.user.exception;

public class UserEmailAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 3150921405582995630L;

	public UserEmailAlreadyExistsException(String message) {
		super(message);
	}
}
