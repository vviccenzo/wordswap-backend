package com.backend.wordswap.user.exception;

public class InvalidCredentialsException extends RuntimeException {

	private static final long serialVersionUID = -3070011709273720444L;

	public InvalidCredentialsException(String message) {
		super(message);
	}
}
