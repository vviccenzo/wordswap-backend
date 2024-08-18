package com.backend.wordswap.message;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@Service
@Transactional
public class MessageService {

	private final MessageRepository messageRepository;

	private final UserRepository userRepository;

	private final ConversationService conversationService;

	MessageService(MessageRepository messageRepository, UserRepository userRepository,
			ConversationService conversationService) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
		this.conversationService = conversationService;
	}

	public List<ConversationResponseDTO> sendMessage(MessageCreateDTO dto) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		ConversationModel conversation = this.conversationService.getOrCreateConversation(dto);
		UserModel sender = this.userRepository.findById(dto.getSenderId()).orElseThrow(EntityNotFoundException::new);
		String content = Encrypt.encrypt(dto.getContent());

		this.messageRepository.save(new MessageModel(content, sender, conversation));

		return this.conversationService.findAllConversationByUserId(dto.getSenderId());
	}

}
