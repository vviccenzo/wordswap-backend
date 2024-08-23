package com.backend.wordswap.websocket;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.message.MessageService;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;

@Controller
public class WebSocketController {

	private MessageService messageService;

	public WebSocketController(MessageService messageService) {
		this.messageService = messageService;
	}

	@MessageMapping("/chat/{roomId}")
	@SendTo("/topic/messages/{roomId}")
	public List<ConversationResponseDTO> sendMessage(@DestinationVariable String roomId,
			@RequestBody MessageCreateDTO dto) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return this.messageService.sendMessage(dto);
	}

	@SendTo("/topic/messages/{roomId}")
	@MessageMapping("/chat/edit/{roomId}")
	public List<ConversationResponseDTO> editMessage(@DestinationVariable String roomId,
			@RequestBody MessageEditDTO dto) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return this.messageService.editMessage(dto);
	}

}
