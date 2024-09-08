package com.backend.wordswap.conversation;

import com.backend.wordswap.conversation.dto.ConversartionDeleteDTO;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.translation.configuration.TranslationConfigDTO;
import com.backend.wordswap.translation.configuration.TranslationConfigurationService;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

	@Autowired
	private ConversationService conversationService;

	@Autowired
	private TranslationConfigurationService translationConfigurationService;

	@GetMapping(path = "/load-conversations", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ConversationResponseDTO> findAllConversationByUserId(@RequestParam("userId") Long userId) {
		return this.conversationService.findAllConversationByUserId(userId);
	}

	@PostMapping(path = "/delete-conversation")
	public void deleteConversartion(@RequestBody ConversartionDeleteDTO dto) {
		this.conversationService.deleteConversartion(dto);
	}

	@PostMapping(path = "/configuration")
	public TranslationConfigResponseDTO configurateTranslation(@RequestBody TranslationConfigDTO dto) {
		return this.translationConfigurationService.configurateTranslation(dto);
	}
}
