package com.backend.wordswap.message;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.message.dto.MessageCreateDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageService messageService;

	@PostMapping(path = "/send-message", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<ConversationResponseDTO> sendMessage(@RequestBody MessageCreateDTO dto) throws Exception {
		return this.messageService.sendMessage(dto);
	}

}
