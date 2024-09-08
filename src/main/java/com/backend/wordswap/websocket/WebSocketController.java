package com.backend.wordswap.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.wordswap.friendship.request.FriendshipRequestService;
import com.backend.wordswap.message.MessageService;

@Controller
public class WebSocketController {

	private final MessageService messageService;

	private final FriendshipRequestService friendshipRequestService;

	public WebSocketController(MessageService messageService, FriendshipRequestService friendshipRequestService) {
		this.messageService = messageService;
		this.friendshipRequestService = friendshipRequestService;
	}

	@MessageMapping("/chat/{roomId}")
	public void handleWebSocketAction(@DestinationVariable String roomId, @RequestBody WebSocketRequest request) throws Exception {
		switch (request.getAction()) {
		case SEND_MESSAGE:
			this.messageService.sendMessage(request.getMessageCreateDTO());
			break;
		case EDIT_MESSAGE:
			this.messageService.editMessage(request.getMessageEditDTO());
			break;
		case DELETE_MESSAGE:
			this.messageService.deleteMessage(request.getMessageDeleteDTO());
			break;
		case SEND_FRIEND_REQUEST:
			this.friendshipRequestService.sendInvite(request.getFriendRequestDTO(), WebSocketAction.SEND_FRIEND_REQUEST);
			break;
		default:
			throw new IllegalArgumentException("Ação desconhecida: " + request.getAction());
		}
	}

}
