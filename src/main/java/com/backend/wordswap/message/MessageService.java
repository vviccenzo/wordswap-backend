package com.backend.wordswap.message;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.gemini.GeminiAPIService;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.TranslationRepository;
import com.backend.wordswap.translation.TranslationService;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.entity.TranslationModel;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageService {

	private final GeminiAPIService geminiAPIService;
	private final TranslationService translationService;
	private final ConversationService conversationService;

	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final TranslationRepository translationRepository;
	private final TranslationConfigurationRepository translationConfigRepository;

	public MessageService(MessageRepository messageRepository, UserRepository userRepository,
			ConversationService conversationService, TranslationService translationService,
			GeminiAPIService geminiAPIService, TranslationRepository translationRepository,
			TranslationConfigurationRepository translationConfigRepository) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.conversationService = conversationService;
		this.translationService = translationService;
		this.geminiAPIService = geminiAPIService;
		this.translationRepository = translationRepository;
		this.translationConfigRepository = translationConfigRepository;
	}

	public List<ConversationResponseDTO> sendMessage(MessageCreateDTO dto) throws Exception {
		ConversationModel conversation = this.conversationService.getOrCreateConversation(dto);
		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);

		String content = this.processContent(dto);

		this.saveMessage(content, sender, conversation);

		return this.conversationService.findAllConversationByUserId(dto.getSenderId());
	}

	private String processContent(MessageCreateDTO dto) throws Exception {
		if (!dto.getIsTranslation().booleanValue()) {
			return Encrypt.encrypt(dto.getContent());
		}

		return this.handleTranslation(dto);
	}

	private String handleTranslation(MessageCreateDTO dto) throws Exception {
		String content = dto.getContent();
		String targetLang = dto.getTargetLanguage();
		String baseLanguage = this.translationService.detectLanguage(content);

		Optional<TranslationModel> optTranslation = this.translationService.findTranslationByContent(baseLanguage,
				targetLang, content);
		if (optTranslation.isPresent()) {
			return optTranslation.get().getContentTranslated();
		}

		List<TranslationConfigurationModel> configs = this.translationConfigRepository
				.findAllByConversationId(dto.getConversationId());

		List<TranslationConfigurationModel> senderConfigs = configs.stream()
				.filter(f -> dto.getSenderId().compareTo(f.getUser().getId()) == 0).toList();

		if (!CollectionUtils.isEmpty(senderConfigs)) {

		}

		List<TranslationConfigurationModel> receiverConfigs = configs.stream()
				.filter(f -> dto.getReceiverId().compareTo(f.getUser().getId()) == 0).toList();

		if (!CollectionUtils.isEmpty(receiverConfigs)) {

		}

		String contentTranslated = this.geminiAPIService.translateText(content, targetLang);

		this.translationRepository.save(new TranslationModel(baseLanguage, content, targetLang, contentTranslated));

		return contentTranslated;
	}

	private void saveMessage(String content, UserModel sender, ConversationModel conversation) {
		MessageModel message = new MessageModel(content, sender, conversation);

		this.messageRepository.save(message);
	}

	public List<ConversationResponseDTO> editMessage(MessageEditDTO dto) throws Exception {
		MessageModel message = this.messageRepository.findById(dto.getId())
				.orElseThrow(() -> new EntityNotFoundException("Message not found."));

		message.setContent(Encrypt.encrypt(dto.getContent()));
		message.setIsEdited(Boolean.TRUE);

		this.messageRepository.save(message);

		return this.conversationService.findAllConversationByUserId(message.getSender().getId());
	}

	public List<ConversationResponseDTO> deleteMessage(MessageDeleteDTO dto) {
		MessageModel message = this.messageRepository.findById(dto.id())
				.orElseThrow(() -> new EntityNotFoundException("Message not found."));

		message.setIsDeleted(Boolean.TRUE);

		this.messageRepository.save(message);

		return this.conversationService.findAllConversationByUserId(message.getSender().getId());
	}
}
