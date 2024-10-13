package com.backend.wordswap.conversation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.backend.wordswap.conversation.dto.ConversartionArchiveDTO;
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
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConversationService {

	private final UserRepository userRepository;
	private final ConversationRepository conversationRepository;
	private final MessageRepository messageRepository;

	public List<ConversationResponseDTO> findAllConversationByUserId(Long userId, Integer pageNumber) {
		UserModel user = this.userRepository.findById(userId).orElseThrow();
		Set<Long> conversationIds = this.getAllConversationIds(user);

		Pageable pageable = PageRequest.of(pageNumber, 30, Sort.by(Sort.Direction.DESC, "sentAt"));
		Map<Long, Long> totalMessagesByConversation = this.getTotalMessagesByConversation(conversationIds);
		Map<Long, List<MessageModel>> messageByConversation = this.getMessageGroupedByConversation(conversationIds, pageable);

		return ConversationFactory.buildConversationsResponse(user, messageByConversation, userId, totalMessagesByConversation);
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
	    return Stream.concat(
	                user.getInitiatedConversations().stream().map(ConversationModel::getId),
	                user.getReceivedConversations().stream().map(ConversationModel::getId)
	           ).collect(Collectors.toSet());
	}

	public Map<Long, List<MessageModel>> getMessageGroupedByConversation(Set<Long> conversationIds, Pageable pageable) {
		return this.messageRepository.findAllByConversationIdIn(conversationIds, pageable).stream()
				.collect(Collectors.groupingBy(msg -> msg.getConversation().getId()));
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
		return dto.getConversationId() != null && dto.getConversationId().compareTo(0L) != 0 ? this.conversationRepository.findById(dto.getConversationId())
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

	public void archiveConversartion(ConversartionArchiveDTO dto) {
		Optional<ConversationModel> optConv = this.conversationRepository.findById(dto.getId());
		if (optConv.isEmpty()) {
			throw new EntityNotFoundException("Conversartion not founded. ID: " + dto.getId());
		}

		ConversationModel conv = optConv.get();

		if (conv.getUserInitiator().getId().compareTo(dto.getUserId()) == 0) {
			conv.setArchivedInitiator(dto.getHasToArchive());
		}

		if (conv.getUserRecipient().getId().compareTo(dto.getUserId()) == 0) {
			conv.setArchivedRecipient(dto.getHasToArchive());
		}

		this.conversationRepository.save(conv);
	}
}
