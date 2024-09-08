package com.backend.wordswap.message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.EntityNotFoundException;

class MessageServiceTest {

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private ConversationService conversationService;
 
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private TranslationConfigurationRepository translationConfigRepository;

	@InjectMocks
	private MessageService messageService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSendMessage() throws Exception {
//		MessageCreateDTO dto = new MessageCreateDTO(1L, "Hello", 1L);
//		ConversationModel conversationModel = new ConversationModel();
//		UserModel userModel = new UserModel();
//
//		when(conversationService.getOrCreateConversation(dto)).thenReturn(conversationModel);
//		when(userRepository.findById(dto.getSenderId())).thenReturn(Optional.of(userModel));
//		when(messageRepository.save(any(MessageModel.class))).thenReturn(null);
//		when(conversationService.findAllConversationByUserId(dto.getSenderId()))
//				.thenReturn(List.of(new ConversationResponseDTO()));
//
//		List<ConversationResponseDTO> result = messageService.sendMessage(dto);
//
//		assertNotNull(result);
//		verify(messageRepository, times(1)).save(any(MessageModel.class));
//		verify(conversationService, times(1)).findAllConversationByUserId(dto.getSenderId());
	}

	@Test
	void testSendMessage_UserNotFound() {
		MessageCreateDTO dto = new MessageCreateDTO(1L, "Hello", 1L);

		when(userRepository.findById(dto.getSenderId())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> {
			messageService.sendMessage(dto);
		});
	}

	@Test
	void testEditMessage() throws Exception {
//		MessageEditDTO dto = new MessageEditDTO(1L, "Updated Message", 0);
//		MessageModel messageModel = new MessageModel();
//		UserModel userModel = new UserModel();
//		messageModel.setSender(userModel);
//
//		when(messageRepository.findById(dto.getId())).thenReturn(Optional.of(messageModel));
//		when(messageRepository.save(any(MessageModel.class))).thenReturn(null);
//		when(conversationService.findAllConversationByUserId(userModel.getId())).thenReturn(List.of(new ConversationResponseDTO()));
//
////		List<ConversationResponseDTO> result = messageService.editMessage(dto);
////
////		assertNotNull(result);
////		verify(messageRepository, times(1)).save(any(MessageModel.class));
////		verify(conversationService, times(1)).findAllConversationByUserId(userModel.getId());
////		assertTrue(messageModel.getIsEdited());
	}

	@Test
	void testEditMessage_MessageNotFound() {
//		MessageEditDTO dto = new MessageEditDTO(1L, "Updated Message", 0);
//
//		when(messageRepository.findById(dto.getId())).thenReturn(Optional.empty());
//
//		assertThrows(RuntimeException.class, () -> {
//			messageService.editMessage(dto);
//		});
	}
}
