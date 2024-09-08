package com.backend.wordswap.message;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.message.dto.MessageRequestDTO;

@RestController
@RequestMapping("/message")
public class MessageController {

	private final MessageService messageService;

	private MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@PostMapping(path = "/get-messages")
	public ConversationResponseDTO getMessages(@RequestBody MessageRequestDTO dto) {
		return this.messageService.getMessages(dto);
	}

}
