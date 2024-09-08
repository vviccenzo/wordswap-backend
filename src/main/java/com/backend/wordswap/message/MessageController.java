package com.backend.wordswap.message;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageRequestDTO;

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

	@PostMapping(path = "/get-messages")
	public ConversationResponseDTO getMessages(MessageRequestDTO dto) {
		return this.getMessages(dto);
	}

}
