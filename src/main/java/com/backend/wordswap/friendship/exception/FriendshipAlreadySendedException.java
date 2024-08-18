package com.backend.wordswap.friendship.exception;

public class FriendshipAlreadySendedException extends RuntimeException {

	private static final long serialVersionUID = -6376534662027124700L;

	public FriendshipAlreadySendedException(String message) {
		super(message);
	}
}
