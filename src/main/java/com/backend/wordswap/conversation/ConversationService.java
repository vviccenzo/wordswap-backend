package com.backend.wordswap.conversation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.backend.wordswap.conversation.dto.ConversartionDeleteDTO;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.conversation.factory.ConversationFactory;
import com.backend.wordswap.message.MessageRepository;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ConversationService {

	private UserRepository userRepository;

	private ConversationRepository conversationRepository;

	private MessageRepository messageRepository;

	public ConversationService(UserRepository userRepository, ConversationRepository conversationRepository, MessageRepository messageRepository) {
		this.userRepository = userRepository;
		this.conversationRepository = conversationRepository;
		this.messageRepository = messageRepository;
	}

	public List<ConversationResponseDTO> findAllConversationByUserId(Long userId, Integer pageNumber) {
		UserModel user = this.userRepository.findById(userId).orElseThrow();
		Set<Long> conversationIds = this.getAllConversationIds(user);

		Pageable pageable = PageRequest.of(pageNumber, 30, Sort.by(Sort.Direction.DESC, "sentAt"));
		Map<Long, Long> totalMessagesByConversation = this.getTotalMessagesByConversation(conversationIds);
		Map<Long, List<MessageModel>> messageByConversation = this.getMessageGroupedByConversation(conversationIds, pageable);
		List<ConversationResponseDTO> conversationResponseDTOS = this.buildConversationsResponse(user, messageByConversation, userId, totalMessagesByConversation);

		return conversationResponseDTOS;
	}
	
	public Map<Long, Long> getTotalMessagesByConversation(Set<Long> conversationIds) {
	    List<Object[]> results = this.messageRepository.findTotalMessagesByConversationIds(conversationIds);
	    Map<Long, Long> totalMessagesByConversation = new HashMap<>();
	    
	    for (Object[] result : results) {
	        Long conversationId = (Long) result[0];
	        Long messageCount = (Long) result[1];

	        totalMessagesByConversation.put(conversationId, messageCount);
	    }
	    
	    return totalMessagesByConversation;
	}

	private Set<Long> getAllConversationIds(UserModel user) {
		Set<Long> conversationIds = user.getInitiatedConversations().stream().map(ConversationModel::getId).collect(Collectors.toSet());

		conversationIds.addAll(user.getReceivedConversations().stream().map(ConversationModel::getId).collect(Collectors.toSet()));

		return conversationIds;
	}

	private Map<Long, List<MessageModel>> getMessageGroupedByConversation(Set<Long> conversationIds, Pageable pageable) {
		Map<Long, List<MessageModel>> messageGrouped = new HashMap<>();
		
		conversationIds.forEach(convId -> messageGrouped.put(convId, this.messageRepository.findAllByConversationId(convId, pageable)));
		
		return messageGrouped;
	}

	private List<ConversationResponseDTO> buildConversationsResponse(UserModel user, Map<Long, List<MessageModel>> messageByConversation, Long userId, Map<Long, Long> totalMessagesByConversation) {
		List<ConversationResponseDTO> conversationResponseDTOS = new ArrayList<>();
		user.getInitiatedConversations().stream().filter(conversation -> !conversation.getIsDeletedInitiator())
				.map(conversation -> ConversationFactory.buildMessages(userId, conversation, messageByConversation, totalMessagesByConversation))
				.forEach(conversationResponseDTOS::add);

		user.getReceivedConversations().stream().filter(conversation -> !conversation.getIsDeletedRecipient())
				.map(conversation -> ConversationFactory.buildMessages(userId, conversation, messageByConversation, totalMessagesByConversation))
				.forEach(conversationResponseDTOS::add);

	    conversationResponseDTOS.sort((c1, c2) -> {
	        LocalDateTime lastMessageC1 = c1.getLastMessage().keySet().stream().findFirst().orElse(LocalDateTime.MIN);
	        LocalDateTime lastMessageC2 = c2.getLastMessage().keySet().stream().findFirst().orElse(LocalDateTime.MIN);

	        return lastMessageC2.compareTo(lastMessageC1);
	    });

		return conversationResponseDTOS;
	}

	@Transactional
	public ConversationModel createNewConversation(MessageCreateDTO dto) {
		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);
		UserModel receiver = this.userRepository.findById(dto.getReceiverId()).orElseThrow(EntityNotFoundException::new);

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

	@Transactional
	public void deleteConversartion(ConversartionDeleteDTO dto) {
		Optional<ConversationModel> optConv = this.conversationRepository.findById(dto.getId());
		if (optConv.isEmpty()) {
			throw new EntityNotFoundException("Conversartion not founded. ID: " + dto.getId());
		}

		ConversationModel conv = optConv.get();

		if (conv.getUserInitiator().getId().compareTo(dto.getUserId()) == 0) {
			conv.setIsDeletedInitiator(Boolean.TRUE);
		}

		if (conv.getUserRecipient().getId().compareTo(dto.getUserId()) == 0) {
			conv.setIsDeletedRecipient(Boolean.TRUE);
		}

		this.conversationRepository.save(conv);
	}
}
