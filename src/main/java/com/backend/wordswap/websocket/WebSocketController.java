package com.backend.wordswap.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.wordswap.friendship.request.FriendshipRequestService;
import com.backend.wordswap.message.MessageService;
import com.backend.wordswap.websocket.definition.WebSocketErrorResponse;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WebSocketController {

	private final MessageService messageService;

	private final FriendshipRequestService friendshipRequestService;

	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/chat/{roomId}")
	public void handleWebSocketAction(@DestinationVariable String roomId, @RequestBody WebSocketRequest request) throws Exception {
	    try {
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
	            case DELETE_FRIEND:
	                this.friendshipRequestService.deleteFriendship(request.getFriendshipDeleteRequestDTO());
	                break;
	            case UPDATE_FRIEND_REQUEST:
	                this.friendshipRequestService.changeStatus(request.getFriendshipRequestUpdateDTO());
	                break;
	            default:
	                throw new IllegalArgumentException("Ação desconhecida: " + request.getAction());
	        }
	    } catch (Exception e) {
	        this.sendErrorMessage(roomId, e.getMessage());
	    }
	}

	private void sendErrorMessage(String roomId, String errorMessage) {
	    WebSocketErrorResponse errorResponse = new WebSocketErrorResponse(errorMessage);
	    this.messagingTemplate.convertAndSend("/topic/errors/" + roomId, errorResponse);
	}
}
