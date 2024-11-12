package com.backend.wordswap.conversation;

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.backend.wordswap.conversation.dto.ConversartionDeleteDTO;
import com.backend.wordswap.conversation.dto.ConversationGroupCreateDTO;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.conversation.entity.ConversationType;
import com.backend.wordswap.conversation.factory.ConversationFactory;
import com.backend.wordswap.conversation.profile.entity.ConversationProfileModel;
import com.backend.wordswap.message.MessageRepository;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.websocket.WebSocketAction;
import com.backend.wordswap.websocket.WebSocketResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConversationService {

	private final UserRepository userRepository;
	private final ConversationRepository conversationRepository;
	private final MessageRepository messageRepository;
	private final SimpMessagingTemplate messagingTemplate;

	public List<ConversationResponseDTO> findAllConversationByUserId(Long userId, Integer pageNumber) {
		UserModel user = this.userRepository.findById(userId).orElseThrow();
		Set<Long> conversationIds = this.getAllConversationIds(user);

		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "sentAt"));
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
	    return user.getConversations().stream().map(ConversationModel::getId).collect(Collectors.toSet());
	}

	public Map<Long, List<MessageModel>> getMessageGroupedByConversation(Set<Long> conversationIds, Pageable pageable) {
	    List<MessageModel> messages = messageRepository.findAllByConversationIdIn(conversationIds, pageable);

	    return messages.stream().collect(Collectors.groupingBy(message -> message.getConversation().getId()));
	}

	@Transactional
	public ConversationModel createNewConversation(MessageCreateDTO dto) {
		Optional<ConversationModel> optConv = this.conversationRepository.findByConversationCode(dto.getConversationCode());
		if(optConv.isPresent()) {
			return optConv.get();
		}

		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);
		UserModel receiver = this.userRepository.findById(dto.getReceiverId()).orElseThrow(EntityNotFoundException::new);

		ConversationModel conversation = new ConversationModel();
		conversation.setConversationCode(LocalDate.now().toString() + "_" + UUID.randomUUID().toString());
		conversation.setCreatedDate(LocalDate.now());
		conversation.setParticipants(List.of(sender, receiver));
		conversation.setType(ConversationType.ONE_TO_ONE);

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

		this.conversationRepository.save(conv);

        List<ConversationResponseDTO> convsSender = this.findAllConversationByUserId(dto.getUserId(), 0);

        this.messagingTemplate.convertAndSend("/topic/messages/" + dto.getUserId(), new WebSocketResponse<>(WebSocketAction.SEND_MESSAGE, convsSender));
	}

	@Transactional
	public void createGroup(ConversationGroupCreateDTO dto) {
		List<UserModel> users = this.userRepository.findAllById(dto.getUserIds());

		ConversationModel conversation = new ConversationModel();
		conversation.setConversationCode(LocalDate.now().toString() + "_" + UUID.randomUUID().toString());
		conversation.setCreatedDate(LocalDate.now());
		conversation.setParticipants(users);
		conversation.setType(ConversationType.GROUP);
		conversation.setConversationName(dto.getName());
		conversation.setConversationBio(dto.getBio());

		if (dto.getImageContent() != null) {
			byte[] imageContent = Base64.getDecoder().decode(dto.getImageContent());
			ConversationProfileModel image = new ConversationProfileModel(conversation, imageContent, dto.getImageFileName(), LocalDate.now());
			conversation.setConversationProfile(image);
		}

		this.conversationRepository.save(conversation);

		dto.getUserIds().forEach(userId -> {
			List<ConversationResponseDTO> convsSender = this.findAllConversationByUserId(userId, 0);
			this.messagingTemplate.convertAndSend("/topic/messages/" + userId, new WebSocketResponse<>(WebSocketAction.SEND_MESSAGE, convsSender));
		});
	}
}
