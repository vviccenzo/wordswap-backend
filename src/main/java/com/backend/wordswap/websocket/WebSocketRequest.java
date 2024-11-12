package com.backend.wordswap.websocket;

import com.backend.wordswap.conversation.dto.ConversationGroupCreateDTO;
import com.backend.wordswap.friendshipRequest.dto.FriendshipDeleteRequestDTO;
import com.backend.wordswap.friendshipRequest.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.friendshipRequest.dto.FriendshipRequestUpdateDTO;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;
import com.backend.wordswap.message.dto.MessageViewDTO;

import lombok.Data;

@Data
public class WebSocketRequest {

	private WebSocketAction action;

	private MessageCreateDTO messageCreateDTO;

	private MessageEditDTO messageEditDTO;

	private MessageDeleteDTO messageDeleteDTO;

	private MessageViewDTO messageViewDTO;

	private FriendshipRequestCreateDTO friendRequestDTO;

	private FriendshipDeleteRequestDTO friendshipDeleteRequestDTO;

	private FriendshipRequestUpdateDTO friendshipRequestUpdateDTO;

	private ConversationGroupCreateDTO conversationGroupCreateDTO;

}
