package com.backend.wordswap.user.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -8464828654826482197L;

	public UsernameAlreadyExistsException(String message) {
		super(message);
	}
}
