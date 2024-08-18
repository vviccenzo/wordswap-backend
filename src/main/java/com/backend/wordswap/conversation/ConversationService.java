package com.backend.wordswap.conversation;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.conversation.factory.ConversationFactory;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationService {

	private UserRepository userRepository;

	private ConversationRepository conversationRepository;

	ConversationService(UserRepository userRepository, ConversationRepository conversationRepository) {
		this.userRepository = userRepository;
		this.conversationRepository = conversationRepository;
	}

	public List<ConversationResponseDTO> findAllConversationByUserId(Long userId) {
		List<ConversationResponseDTO> conversationResponseDTOS = new ArrayList<>();
		ConversationFactory conversationFactory = new ConversationFactory();
		UserModel user = this.userRepository.findById(userId).orElseThrow();

		user.getInitiatedConversations().forEach(conversationModel -> conversationResponseDTOS
				.add(conversationFactory.buildMessages(userId, conversationModel)));

		user.getReceivedConversations().forEach(conversationModel -> conversationResponseDTOS
				.add(conversationFactory.buildMessages(userId, conversationModel)));

		return conversationResponseDTOS;
	}

	@Transactional
	public ConversationModel createNewConversation(MessageCreateDTO dto) {
		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);
		UserModel receiver = this.userRepository.findById(dto.getReceiverId())
				.orElseThrow(EntityNotFoundException::new);

		ConversationModel conversation = new ConversationModel();
		conversation.setCreatedDate(LocalDate.now());
		conversation.setUserInitiator(sender);
		conversation.setUserRecipient(receiver);

		return this.conversationRepository.save(conversation);
	}

	public ConversationModel getOrCreateConversation(MessageCreateDTO dto) {
		return dto.getConversationId() != null ? this.conversationRepository.findById(dto.getConversationId())
				.orElseThrow(EntityNotFoundException::new) : this.createNewConversation(dto);
	}
}
