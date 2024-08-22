package com.backend.wordswap.conversation;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

	@Autowired
	private ConversationService conversationService;

	@GetMapping(path = "/load-conversations", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ConversationResponseDTO> findAllConversationByUserId(@RequestParam("userId") Long userId) {
		return this.conversationService.findAllConversationByUserId(userId);
	}

}
