package com.backend.wordswap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.conversation.ConversationRepository;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.translation.configuration.TranslationConfigurationService;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigDTO;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserNotFoundException;

import jakarta.persistence.EntityNotFoundException;

public class TranslationConfigurationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ConversationRepository convRepository;

	@Mock
	private TranslationConfigurationRepository translationConfigurationRepository;

	@InjectMocks
	private TranslationConfigurationService translationService;

	private UserModel user;
	private ConversationModel conversation;

	@BeforeEach
	void setUp() {
		user = new UserModel();
		user.setId(1L);

		conversation = new ConversationModel();
		conversation.setId(1L);
		
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testConfigurateTranslation_Success() {
		TranslationConfigDTO dto = new TranslationConfigDTO();
		dto.setUserId(1L);
		dto.setConversationId(1L);
		dto.setReceivingTranslation("pt-BR");
		dto.setIsReceivingTranslation(true);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(convRepository.findById(1L)).thenReturn(Optional.of(conversation));

		TranslationConfigResponseDTO response = translationService.configurateTranslation(dto);

		verify(translationConfigurationRepository).deleteAllByUserIdAndConversationId(1L, 1L);

		assertNotNull(response);
		assertTrue(response.getIsReceivingTranslation());
		assertEquals("pt-BR", response.getReceivingTranslation());
	}

	@Test
	void testConfigurateTranslation_UserNotFound() {
		TranslationConfigDTO dto = new TranslationConfigDTO();
		dto.setUserId(1L);
		dto.setConversationId(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		Exception exception = assertThrows(UserNotFoundException.class, () -> {
			translationService.configurateTranslation(dto);
		});

		String expectedMessage = "Usuário não encontrado com o id: 1";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testConfigurateTranslation_ConversationNotFound() {
		TranslationConfigDTO dto = new TranslationConfigDTO();
		dto.setUserId(1L);
		dto.setConversationId(1L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(convRepository.findById(1L)).thenReturn(Optional.empty());

		Exception exception = assertThrows(EntityNotFoundException.class, () -> {
			translationService.configurateTranslation(dto);
		});

		String expectedMessage = "Conversa não encontrada com o id: 1";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
}
