package com.backend.wordswap.websocket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.friendshipRequest.FriendshipRequestService;
import com.backend.wordswap.friendshipRequest.dto.FriendshipDeleteRequestDTO;
import com.backend.wordswap.friendshipRequest.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.friendshipRequest.dto.FriendshipRequestUpdateDTO;
import com.backend.wordswap.friendshipRequest.entity.enumeration.StatusType;
import com.backend.wordswap.message.MessageService;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;

class WebSocketControllerTest {

	@InjectMocks
	private WebSocketController webSocketController;

	@Mock
	private MessageService messageService;

	@Mock
	private FriendshipRequestService friendshipRequestService;

	private WebSocketRequest webSocketRequest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		webSocketRequest = new WebSocketRequest();
	}

	@Test
	void testHandleWebSocketActionSendMessage() throws Exception {
		webSocketRequest.setAction(WebSocketAction.SEND_MESSAGE);
		webSocketRequest.setMessageCreateDTO(new MessageCreateDTO());

		webSocketController.handleWebSocketAction("roomId", webSocketRequest);

		verify(messageService).sendMessage(any(MessageCreateDTO.class));
	}

	@Test
	void testHandleWebSocketActionEditMessage() throws Exception {
		webSocketRequest.setAction(WebSocketAction.EDIT_MESSAGE);
		webSocketRequest.setMessageEditDTO(new MessageEditDTO());

		webSocketController.handleWebSocketAction("roomId", webSocketRequest);

		verify(messageService).editMessage(any(MessageEditDTO.class));
	}

	@Test
	void testHandleWebSocketActionDeleteMessage() throws Exception {
		webSocketRequest.setAction(WebSocketAction.DELETE_MESSAGE);
		webSocketRequest.setMessageDeleteDTO(new MessageDeleteDTO(1L));

		webSocketController.handleWebSocketAction("roomId", webSocketRequest);

		verify(messageService).deleteMessage(any(MessageDeleteDTO.class));
	}

	@Test
	void testHandleWebSocketActionSendFriendRequest() throws Exception {
		webSocketRequest.setAction(WebSocketAction.SEND_FRIEND_REQUEST);
		webSocketRequest.setFriendRequestDTO(new FriendshipRequestCreateDTO());

		webSocketController.handleWebSocketAction("roomId", webSocketRequest);

		verify(friendshipRequestService).sendInvite(any(FriendshipRequestCreateDTO.class), eq(WebSocketAction.SEND_FRIEND_REQUEST));
	}

	@Test
	void testHandleWebSocketActionDeleteFriend() throws Exception {
		webSocketRequest.setAction(WebSocketAction.DELETE_FRIEND);
		webSocketRequest.setFriendshipDeleteRequestDTO(new FriendshipDeleteRequestDTO(1L, 2L));

		webSocketController.handleWebSocketAction("roomId", webSocketRequest);

		verify(friendshipRequestService).deleteFriendship(any(FriendshipDeleteRequestDTO.class));
	}

	@Test
	void testHandleWebSocketActionUpdateFriendRequest() throws Exception {
		webSocketRequest.setAction(WebSocketAction.UPDATE_FRIEND_REQUEST);
		webSocketRequest.setFriendshipRequestUpdateDTO(new FriendshipRequestUpdateDTO(1L, StatusType.ACCEPTED));

		webSocketController.handleWebSocketAction("roomId", webSocketRequest);

		verify(friendshipRequestService).changeStatus(any(FriendshipRequestUpdateDTO.class));
	}
}
