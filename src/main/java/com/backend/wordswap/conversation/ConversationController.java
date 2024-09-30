package com.backend.wordswap.conversation;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.conversation.dto.ConversartionArchiveDTO;
import com.backend.wordswap.conversation.dto.ConversartionDeleteDTO;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.translation.configuration.TranslationConfigurationService;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigDTO;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/conversation")
public class ConversationController {

	private final ConversationService conversationService;

	private final TranslationConfigurationService translationConfigurationService;

	@GetMapping(path = "/load-conversations", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ConversationResponseDTO> findAllConversationByUserId(@RequestParam("userId") Long userId) {
		return this.conversationService.findAllConversationByUserId(userId, 0);
	}

	@PostMapping(path = "/delete-conversation")
	public void deleteConversartion(@RequestBody ConversartionDeleteDTO dto) {
		this.conversationService.deleteConversartion(dto);
	}

	@PostMapping(path = "/configuration")
	public TranslationConfigResponseDTO configurateTranslation(@RequestBody TranslationConfigDTO dto) {
		return this.translationConfigurationService.configurateTranslation(dto);
	}

	@PutMapping(path = "/archive-conversation")
	public void deleteConversartion(@RequestBody ConversartionArchiveDTO dto) {
		this.conversationService.archiveConversartion(dto);
	}
}
