package com.backend.wordswap.websocket.definition;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebSocketErrorResponse {

	private String errorMessage;

}
