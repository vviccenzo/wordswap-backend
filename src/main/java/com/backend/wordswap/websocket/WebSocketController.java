package com.backend.wordswap.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.wordswap.websocket.definition.WebSocketRequest;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WebSocketController {

	private final WebSocketHandler handler;

	@MessageMapping("/chat/{roomId}")
	public void handleWebSocketAction(@DestinationVariable String roomId, @RequestBody WebSocketRequest request) throws Exception {
		this.handler.handleAction(roomId, request);
	}

}
