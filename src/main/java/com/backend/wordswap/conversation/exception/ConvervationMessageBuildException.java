package com.backend.wordswap.conversation.exception;

import java.security.GeneralSecurityException;

public class ConvervationMessageBuildException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConvervationMessageBuildException(GeneralSecurityException e) {
		super(e);
	}
}
