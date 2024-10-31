package com.backend.wordswap.conversation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.conversation.dto.ConversartionArchiveDTO;
import com.backend.wordswap.conversation.dto.ConversartionDeleteDTO;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.translation.configuration.TranslationConfigurationService;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigDTO;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;

class ConversationControllerTest {

	@InjectMocks
	private ConversationController conversationController;

	@Mock
	private ConversationService conversationService;

	@Mock
	private TranslationConfigurationService translationConfigurationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindAllConversationByUserId() {
		Long userId = 1L;
		ConversationResponseDTO conversation = new ConversationResponseDTO();
		when(conversationService.findAllConversationByUserId(userId, 0)).thenReturn(List.of(conversation));

		List<ConversationResponseDTO> result = conversationController.findAllConversationByUserId(userId);

		verify(conversationService, times(1)).findAllConversationByUserId(userId, 0);
		assertEquals(1, result.size());
		assertEquals(conversation, result.get(0));
	}

	@Test
	void testDeleteConversation() {
		ConversartionDeleteDTO dto = new ConversartionDeleteDTO(1l, 1l);

		conversationController.deleteConversartion(dto);

		verify(conversationService, times(1)).deleteConversartion(dto);
	}

	@Test
	void testConfigurateTranslation() {
		TranslationConfigDTO dto = new TranslationConfigDTO();
		TranslationConfigResponseDTO responseDto = new TranslationConfigResponseDTO();

		when(translationConfigurationService.configurateTranslation(dto)).thenReturn(responseDto);

		TranslationConfigResponseDTO result = conversationController.configurateTranslation(dto);

		verify(translationConfigurationService, times(1)).configurateTranslation(dto);
		assertEquals(responseDto, result);
	}
}
