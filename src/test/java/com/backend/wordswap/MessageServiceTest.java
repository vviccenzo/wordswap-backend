package com.backend.wordswap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.message.MessageRepository;
import com.backend.wordswap.message.MessageService;
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

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private MessageService messageService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSendMessage() throws Exception {
		MessageCreateDTO dto = new MessageCreateDTO(1L, "Hello", 1L);
		ConversationModel conversationModel = new ConversationModel();
		UserModel userModel = new UserModel();

		when(this.conversationService.getOrCreateConversation(dto)).thenReturn(conversationModel);
		when(this.userRepository.findById(dto.getSenderId())).thenReturn(Optional.of(userModel));
		when(this.messageRepository.save(any(MessageModel.class))).thenReturn(null);
		when(this.conversationService.findAllConversationByUserId(dto.getSenderId(), 0))
				.thenReturn(List.of(new ConversationResponseDTO()));

		this.messageService.sendMessage(dto);

		verify(this.messageRepository, times(1)).save(any(MessageModel.class));
		verify(this.conversationService, times(2)).findAllConversationByUserId(dto.getSenderId(), 0);
	}

	@Test
	void testSendMessage_UserNotFound() {
		MessageCreateDTO dto = new MessageCreateDTO(1L, "Hello", 1L);

		when(this.userRepository.findById(dto.getSenderId())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> {
			this.messageService.sendMessage(dto);
		});
	}

	@Test
	void testEditMessage() throws Exception {
		MessageEditDTO dto = new MessageEditDTO(1L, 1L, "Updated Message", 0);

		MessageModel messageModel = new MessageModel();
		messageModel.setId(dto.getId());

		ConversationModel conversationModel = new ConversationModel();

		UserModel userInitiator = new UserModel();
		userInitiator.setId(1L);

		UserModel userRecipient = new UserModel();
		userRecipient.setId(2L);

		conversationModel.setUserInitiator(userInitiator);
		conversationModel.setUserRecipient(userRecipient);

		messageModel.setConversation(conversationModel);

		when(this.messageRepository.findById(dto.getId())).thenReturn(Optional.of(messageModel));
		when(this.messageRepository.save(any(MessageModel.class))).thenReturn(null);
		when(this.conversationService.findAllConversationByUserId(userInitiator.getId(), 0))
				.thenReturn(List.of(new ConversationResponseDTO()));
		when(this.conversationService.findAllConversationByUserId(userRecipient.getId(), 0))
				.thenReturn(List.of(new ConversationResponseDTO()));

		this.messageService.editMessage(dto);

		verify(this.messageRepository, times(1)).save(any(MessageModel.class));
		verify(this.conversationService, times(1)).findAllConversationByUserId(userInitiator.getId(), 0);
		verify(this.conversationService, times(1)).findAllConversationByUserId(userRecipient.getId(), 0);
	}

	@Test
	void testEditMessage_MessageNotFound() {
		MessageEditDTO dto = new MessageEditDTO(1L, 1L, "Updated Message", 0);

		when(this.messageRepository.findById(dto.getId())).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> {
			this.messageService.editMessage(dto);
		});
	}
}
