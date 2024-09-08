package com.backend.wordswap.websocket;

import com.backend.wordswap.friendship.request.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;

import lombok.Data;

@Data
public class WebSocketRequest {

	private WebSocketAction action;

	private MessageCreateDTO messageCreateDTO;

	private MessageEditDTO messageEditDTO;

	private MessageDeleteDTO messageDeleteDTO;

	private FriendshipRequestCreateDTO friendRequestDTO;

}
