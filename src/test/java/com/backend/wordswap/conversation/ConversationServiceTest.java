package com.backend.wordswap.conversation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.conversation.dto.ConversartionDeleteDTO;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.message.MessageRepository;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.profile.entity.UserProfileModel;

import jakarta.persistence.EntityNotFoundException;

class ConversationServiceTest {

	@InjectMocks
	private ConversationService conversationService;

	@Mock
	private ConversationRepository conversationRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MessageRepository messageRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindAllConversationByUserId() {
		Long userId = 1L;
		UserModel user = new UserModel();
		user.setId(userId);

		UserProfileModel profilePic = new UserProfileModel();
		profilePic.setUser(user);
		profilePic.setContent(new byte[]{1, 2, 3});
		profilePic.setFileName("profile.jpg");
		profilePic.setUpdateDate(LocalDate.now());

		user.setUserProfile(profilePic);
		
		Long userId2 = 1L;
		UserModel user2 = new UserModel();
		user2.setId(userId2);
		
		UserProfileModel profilePic2 = new UserProfileModel();
		profilePic2.setUser(user);
		profilePic2.setContent(new byte[]{1, 2, 3});
		profilePic2.setFileName("profile.jpg");
		profilePic2.setUpdateDate(LocalDate.now());

		user2.setUserProfile(profilePic2);

		ConversationModel conversation1 = new ConversationModel();
		conversation1.setId(1L);
		conversation1.setUserInitiator(user);
		conversation1.setUserRecipient(user2);
		conversation1.setIsDeletedInitiator(false);

		ConversationModel conversation2 = new ConversationModel();
		conversation2.setId(2L);
		conversation2.setUserInitiator(user);
		conversation2.setUserRecipient(user2);
		conversation2.setIsDeletedRecipient(false);

		user.setInitiatedConversations(List.of(conversation1));
		user.setReceivedConversations(List.of(conversation2));

		when(this.userRepository.findById(userId)).thenReturn(Optional.of(user));

		List<ConversationResponseDTO> result = this.conversationService.findAllConversationByUserId(userId, 0);

		assertEquals(2, result.size());
		verify(this.userRepository, times(1)).findById(userId);
	}

	@Test
	void testCreateNewConversation() {
		MessageCreateDTO dto = new MessageCreateDTO();
		dto.setSenderId(1L);
		dto.setReceiverId(2L);

		UserModel sender = new UserModel();
		sender.setId(1L);

		UserModel receiver = new UserModel();
		receiver.setId(2L);

		when(this.userRepository.findById(1L)).thenReturn(Optional.of(sender));
		when(this.userRepository.findById(2L)).thenReturn(Optional.of(receiver));

		ConversationModel conversation = new ConversationModel();
		when(this.conversationRepository.save(any(ConversationModel.class))).thenReturn(conversation);

		ConversationModel result = this.conversationService.createNewConversation(dto);

		assertNotNull(result);
		verify(this.userRepository, times(1)).findById(1L);
		verify(this.userRepository, times(1)).findById(2L);
		verify(this.conversationRepository, times(1)).save(any(ConversationModel.class));
	}

	@Test
	void testGetOrCreateConversation_ExistingConversation() {
		MessageCreateDTO dto = new MessageCreateDTO();
		dto.setConversationId(1L);

		ConversationModel conversation = new ConversationModel();
		conversation.setId(1L);

		when(this.conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

		ConversationModel result = this.conversationService.getOrCreateConversation(dto);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		verify(conversationRepository, times(1)).findById(1L);
	}

	@Test
	void testGetOrCreateConversation_NewConversation() {
	    MessageCreateDTO dto = new MessageCreateDTO();
	    dto.setSenderId(1L);
	    dto.setReceiverId(2L);
	    dto.setConversationId(null);

	    UserModel sender = new UserModel();
	    sender.setId(1L);
	    UserModel receiver = new UserModel();
	    receiver.setId(2L);

	    when(this.userRepository.findById(1L)).thenReturn(Optional.of(sender));
	    when(this.userRepository.findById(2L)).thenReturn(Optional.of(receiver));

	    ConversationModel newConversation = new ConversationModel();
	    when(this.conversationRepository.save(any(ConversationModel.class))).thenReturn(newConversation);

	    ConversationModel result = this.conversationService.getOrCreateConversation(dto);

	    assertNotNull(result);

	    verify(this.userRepository, times(1)).findById(1L);
	    verify(this.userRepository, times(1)).findById(2L);
	    verify(this.conversationRepository, times(1)).save(any(ConversationModel.class));
	}

	@Test
	void deleteConversation_ShouldThrowException_WhenConversationNotFound() {
		Long conversationId = 1L;
		Long userId = 1L;
		ConversartionDeleteDTO dto = new ConversartionDeleteDTO(conversationId, userId);

		when(this.conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> this.conversationService.deleteConversartion(dto));

		verify(this.conversationRepository, never()).save(any());
	}

	@Test
	void deleteConversation_ShouldSetIsDeletedInitiator_WhenUserIsInitiator() {
		Long conversationId = 1L;
		Long userId = 1L;
		ConversartionDeleteDTO dto = new ConversartionDeleteDTO(conversationId, userId);

		UserModel initiator = new UserModel();
		initiator.setId(userId);

		UserModel recipient = new UserModel();
		recipient.setId(2L);

		ConversationModel conversation = new ConversationModel();
		conversation.setId(conversationId);
		conversation.setUserInitiator(initiator);
		conversation.setUserRecipient(recipient);

		when(this.conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

		this.conversationService.deleteConversartion(dto);

		verify(this.conversationRepository, times(1)).save(conversation);
		assert (conversation.getIsDeletedInitiator());
	}

	@Test
	void deleteConversation_ShouldSetIsDeletedRecipient_WhenUserIsRecipient() {
		Long conversationId = 1L;
		Long userId = 2L;
		ConversartionDeleteDTO dto = new ConversartionDeleteDTO(conversationId, userId);

		UserModel initiator = new UserModel();
		initiator.setId(1L);

		UserModel recipient = new UserModel();
		recipient.setId(userId);

		ConversationModel conversation = new ConversationModel();
		conversation.setId(conversationId);
		conversation.setUserInitiator(initiator);
		conversation.setUserRecipient(recipient);

		when(this.conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

		this.conversationService.deleteConversartion(dto);

		verify(this.conversationRepository, times(1)).save(conversation);
		assert (conversation.getIsDeletedRecipient());
	}

}
