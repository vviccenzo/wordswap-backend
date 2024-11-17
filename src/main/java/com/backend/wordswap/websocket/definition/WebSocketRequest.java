package com.backend.wordswap.websocket.definition;

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

	private MessageEditDTO messageEditDTO;

	private MessageViewDTO messageViewDTO;

	private MessageDeleteDTO messageDeleteDTO;

	private MessageTypingDTO messageTypingDTO;

	private MessageCreateDTO messageCreateDTO;

	private FriendshipRequestCreateDTO friendRequestDTO;

	private FriendshipDeleteRequestDTO friendshipDeleteRequestDTO;

	private FriendshipRequestUpdateDTO friendshipRequestUpdateDTO;

	private ConversationGroupCreateDTO conversationGroupCreateDTO;

    public Object getDtoBasedOnAction() {
        switch (action) {
            case SEND_MESSAGE:
                return messageCreateDTO;
            case EDIT_MESSAGE:
                return messageEditDTO;
            case DELETE_MESSAGE:
                return messageDeleteDTO;
            case VIEW_MESSAGE:
                return messageViewDTO;
            case SEND_FRIEND_REQUEST:
                return friendRequestDTO;
            case UPDATE_FRIEND_REQUEST:
                return friendshipDeleteRequestDTO;
            case ACCEPT_FRIEND_REQUEST:
                return friendshipRequestUpdateDTO;
            case CREATE_GROUP:
                return conversationGroupCreateDTO;
            case USER_TYPING:
                return messageTypingDTO;
            default:
                return null;
        }
    }
}
