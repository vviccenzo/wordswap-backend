package com.backend.wordswap.websocket;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.message.MessageService;
import com.backend.wordswap.message.dto.MessageCreateDTO;

@Controller
public class WebSocketController {

	private MessageService messageService;

	public WebSocketController(MessageService messageService) {
		this.messageService = messageService;
	}

	@SendTo("/topic/messages")
	@MessageMapping("/chat")
	public List<ConversationResponseDTO> sendMessage(MessageCreateDTO dto) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return this.messageService.sendMessage(dto);
	}

}
