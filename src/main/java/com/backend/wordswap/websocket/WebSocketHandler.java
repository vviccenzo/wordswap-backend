package com.backend.wordswap.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.backend.wordswap.websocket.definition.WebSocketAction;
import com.backend.wordswap.websocket.definition.WebSocketActionHandler;
import com.backend.wordswap.websocket.definition.WebSocketErrorResponse;
import com.backend.wordswap.websocket.definition.WebSocketRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebSocketHandler {

	private final SimpMessagingTemplate messagingTemplate;

	public void handleAction(String roomId, WebSocketRequest request) {
		WebSocketAction action = request.getAction();
		WebSocketActionHandler actionHandler = WebSocketActionHandler.getHandler(action);

		try {
			actionHandler.execute(request);
		} catch (Exception e) {
			this.handleError(roomId, e);
		}
	}

	private void handleError(String roomId, Exception e) {
		e.printStackTrace();
		this.sendErrorMessage(roomId, e.getMessage());
	}

	private void sendErrorMessage(String roomId, String errorMessage) {
		WebSocketErrorResponse errorResponse = new WebSocketErrorResponse(errorMessage);
		messagingTemplate.convertAndSend("/topic/errors/" + roomId, errorResponse);
	}
}
