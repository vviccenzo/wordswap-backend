package com.backend.wordswap.message;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageRequestDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/message")
public class MessageController {

	private MessageService messageService;

	@PostMapping(path = "/get-messages")
	public ConversationResponseDTO getMessages(@RequestBody MessageRequestDTO dto) {
		return this.messageService.getMessages(dto);
	}

	@PostMapping(path = "/send-image")
	public void sendImage(@RequestBody MessageCreateDTO dto) throws Exception {
		this.messageService.sendMessage(dto);
	}

}
