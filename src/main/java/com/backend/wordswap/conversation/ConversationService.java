package com.backend.wordswap.conversation;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.conversation.factory.ConversationFactory;
import com.backend.wordswap.message.MessageRepository;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.conversation.dto.ConversartionDeleteDTO;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public List<ConversationResponseDTO> findAllConversationByUserId(Long userId, int pageNumber) {
		UserModel user = this.userRepository.findById(userId).orElseThrow();

		Set<Long> conversationsId = user.getInitiatedConversations().stream().map(ConversationModel::getId).collect(Collectors.toSet());
		conversationsId.addAll(user.getReceivedConversations().stream().map(ConversationModel::getId).collect(Collectors.toSet()));

		Pageable pageable = PageRequest.of(pageNumber, 50, Sort.by(Sort.Direction.DESC, "sentAt"));

		Map<Long, List<MessageModel>> messageByConversation = this.messageRepository
				.findAllByConversationIdIn(conversationsId, pageable).stream()
				.collect(Collectors.groupingBy(msg -> msg.getConversation().getId()));

		return Stream
				.concat(user.getInitiatedConversations().stream().filter(f -> !f.getIsDeletedInitiator()),
						user.getReceivedConversations().stream().filter(f -> !f.getIsDeletedRecipient()))
				.map(conversationModel -> ConversationFactory.buildMessages(userId, conversationModel,
						messageByConversation))
				.toList();
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
