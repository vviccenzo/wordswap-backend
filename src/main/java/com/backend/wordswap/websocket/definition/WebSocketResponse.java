package com.backend.wordswap.websocket.definition;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebSocketResponse<T> {

	private WebSocketAction eventType;

	private T data;

}