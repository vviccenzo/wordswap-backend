package com.backend.wordswap.message;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.gemini.GeminiAPIService;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;
import com.backend.wordswap.message.dto.MessageRequestDTO;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.TranslationConfigurationRepository;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.EntityNotFoundException;

class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private GeminiAPIService geminiAPIService;

    @Mock
    private ConversationService conversationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private TranslationConfigurationRepository translationConfigRepository;

    private UserModel sender;
    private ConversationModel conversation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sender = new UserModel();
        sender.setId(1L);
        
        conversation = new ConversationModel();
    }

    @Test
    void testSendMessageSuccessfully() throws Exception {
        MessageCreateDTO dto = new MessageCreateDTO();
        dto.setSenderId(1L);
        dto.setReceiverId(2L);
        dto.setConversationId(1L);
        dto.setContent("Hello");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(conversationService.getOrCreateConversation(dto)).thenReturn(conversation);
        when(translationConfigRepository.findAllByConversationIdAndUserId(any(), any())).thenReturn(Collections.emptyList());
        
        messageService.sendMessage(dto);
        
        verify(messageRepository, times(1)).save(any(MessageModel.class));
        verify(conversationService, times(2)).findAllConversationByUserId(any(), any());
    }

    @Test
    void testSendMessageUserNotFound() {
        MessageCreateDTO dto = new MessageCreateDTO();
        dto.setSenderId(1L);
        
        when(this.userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            this.messageService.sendMessage(dto);
        });

        assertTrue(exception instanceof EntityNotFoundException);
    }

    @Test
    void testSendMessageWithTranslation() throws Exception {
        MessageCreateDTO dto = new MessageCreateDTO();
        dto.setSenderId(1L);
        dto.setReceiverId(2L);
        dto.setConversationId(1L);
        dto.setContent("Hello");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(conversationService.getOrCreateConversation(dto)).thenReturn(conversation);
        when(translationConfigRepository.findAllByConversationIdAndUserId(any(), any()))
                .thenReturn(Collections.singletonList(new TranslationConfigurationModel()));

        when(geminiAPIService.validateContent(dto.getContent())).thenReturn("Mensagem Válida".trim());
        when(geminiAPIService.improveText(any())).thenReturn("Improved Hello");
        when(geminiAPIService.translateText(any(), any())).thenReturn("Translated Hello");

        this.messageService.sendMessage(dto);

        verify(messageRepository, times(1)).save(any(MessageModel.class));
    }

    @Test
    void testEditMessageSuccessfully() throws Exception {
        MessageModel message = new MessageModel();
        message.setId(1L);
        message.setContent(Encrypt.encrypt("Original Content"));
        message.setIsEdited(false);

        ConversationModel conv = new ConversationModel();
//        conv.setUserInitiator(sender);
//        conv.setUserRecipient(sender);
        message.setConversation(conv);

        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

//        List<TranslationConfigurationModel> senderConfigs = Collections.emptyList();
//        List<TranslationConfigurationModel> receiverConfigs = Collections.emptyList();
//        when(this.messageService.getReceiverTranslationConfigs(conv.getId(), conv.getUserInitiator().getId()))
//            .thenReturn(senderConfigs);
//        when(this.messageService.getReceiverTranslationConfigs(conv.getId(), conv.getUserRecipient().getId()))
//            .thenReturn(receiverConfigs);

        String newContent = "Edited Content";
        MessageEditDTO messageEditDTO = new MessageEditDTO(1L, 1L, newContent, 0);

        this.messageService.editMessage(messageEditDTO);

        assertEquals(Encrypt.encrypt(newContent), message.getContent());
        assertTrue(message.getIsEdited());
        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void testEditMessageNotFound() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            messageService.editMessage(new MessageEditDTO(1L, 1L, "New Content", 0));
        });

        assertEquals("Message not found.", exception.getMessage());
    }

    @Test
    void testDeleteMessageSuccessfully() {
        MessageModel message = new MessageModel();
        ConversationModel conv =  new ConversationModel();
//        conv.setUserInitiator(sender);
//        conv.setUserRecipient(sender);

        message.setId(1L);
        message.setConversation(conv);

        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

        messageService.deleteMessage(new MessageDeleteDTO(1L));

        assertTrue(message.getIsDeleted());
        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void testDeleteMessageNotFound() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            messageService.deleteMessage(new MessageDeleteDTO(1L));
        });

        assertEquals("Message not found.", exception.getMessage());
    }

//    @Test
//    void testImproveContentIfActive() throws Exception {
//        TranslationConfigurationModel config = new TranslationConfigurationModel();
//        config.setIsActive(true);
//
//        when(geminiAPIService.improveText("Hello")).thenReturn("Improved Hello");
//
//        String result = messageService.improveContentIfActive(config, "Hello");
//
//        assertEquals("Improved Hello", result);
//
//        verify(geminiAPIService, times(1)).improveText("Hello");
//    }
//
//    @Test
//    void testImproveContentIfNotActive() throws JsonProcessingException {
//        TranslationConfigurationModel config = new TranslationConfigurationModel();
//        config.setIsActive(false);
//
//        String result = messageService.improveContentIfActive(config, "Hello");
//        assertEquals("Hello", result);
//    }
//
//    @Test
//    void testTranslateContentIfActive() throws Exception {
//        TranslationConfigurationModel config = new TranslationConfigurationModel();
//        config.setIsActive(true);
//        config.setTargetLanguage("fr");
//
//        when(geminiAPIService.translateText("Hello", "fr")).thenReturn("Translated Hello");
//
//        String result = this.messageService.translateContentIfActive(config, "Hello");
//
//        assertEquals("Translated Hello", result);
//
//        verify(this.geminiAPIService, times(1)).translateText("Hello", "fr");
//    }
//
//    @Test
//    void testTranslateContentIfNotActive() throws JsonProcessingException {
//        TranslationConfigurationModel config = new TranslationConfigurationModel();
//        config.setIsActive(false);
//
//        String result = messageService.translateContentIfActive(config, "Hello");
//        assertEquals("Hello", result);
//    }

    @Test
    void testGetMessagesSuccessfully() {
        MessageRequestDTO requestDTO = new MessageRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setConversationId(1L);
        requestDTO.setPageNumber(0);
        
        ConversationResponseDTO dto = new ConversationResponseDTO();
        dto.setId(1L);

        when(conversationService.findAllConversationByUserId(1L, 0)).thenReturn(List.of(dto));
        conversation.setId(1L);

        ConversationResponseDTO result = this.messageService.getMessages(requestDTO);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetMessagesConversationNotFound() {
        MessageRequestDTO requestDTO = new MessageRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setConversationId(1L);
        requestDTO.setPageNumber(0);

        when(conversationService.findAllConversationByUserId(1L, 0)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            messageService.getMessages(requestDTO);
        });

        assertEquals("Conversation not found", exception.getMessage());
    }
    
    @Test
    void testSendMessageWithImage() throws Exception {
        MessageCreateDTO dto = new MessageCreateDTO();
        dto.setSenderId(1L);
        dto.setReceiverId(2L);
        dto.setConversationId(1L);
        dto.setContent("Hello with image");
        dto.setImageContent(Base64.getEncoder().encodeToString("imageData".getBytes()));
        dto.setImageFileName("image.png");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(conversationService.getOrCreateConversation(dto)).thenReturn(conversation);
        when(translationConfigRepository.findAllByConversationIdAndUserId(any(), any())).thenReturn(Collections.emptyList());
        when(geminiAPIService.validateContent(anyString())).thenReturn("Hello with image");
        
        messageService.sendMessage(dto);
        
        ArgumentCaptor<MessageModel> messageCaptor = ArgumentCaptor.forClass(MessageModel.class);
        verify(messageRepository).save(messageCaptor.capture());
        MessageModel savedMessage = messageCaptor.getValue();
        
        assertNotNull(savedMessage.getImage());
        assertArrayEquals("imageData".getBytes(), savedMessage.getImage().getContent());
        assertEquals("image.png", savedMessage.getImage().getFileName());
        assertEquals(LocalDate.now(), savedMessage.getImage().getUploadDate());
    }
    
    @Test
    void testGetTranslationConfigWithActiveConfig() {
        TranslationConfigurationModel activeConfig = new TranslationConfigurationModel();
        activeConfig.setType(TranslationType.RECEIVING);
        activeConfig.setIsActive(true);
        
        TranslationConfigurationModel inactiveConfig = new TranslationConfigurationModel();
        inactiveConfig.setType(TranslationType.RECEIVING);
        inactiveConfig.setIsActive(false);
        
        List<TranslationConfigurationModel> configs = List.of(activeConfig, inactiveConfig);
        
        TranslationConfigurationModel result = messageService.getTranslationConfig(configs, TranslationType.RECEIVING);
        
        assertNotNull(result);
        assertTrue(result.getIsActive());
    }

    @Test
    void testGetTranslationConfigWithNoMatchingActiveConfig() {
        TranslationConfigurationModel inactiveConfig = new TranslationConfigurationModel();
        inactiveConfig.setType(TranslationType.IMPROVING);
        inactiveConfig.setIsActive(false);
        
        List<TranslationConfigurationModel> configs = List.of(inactiveConfig);
        
        TranslationConfigurationModel result = messageService.getTranslationConfig(configs, TranslationType.RECEIVING);
        
        assertNull(result);
    }

	@Test
	void testProcessContentWithExceptionDuringDecryption() throws Exception {
		MessageCreateDTO dto = new MessageCreateDTO();
		dto.setSenderId(1L);
		dto.setReceiverId(2L);
		dto.setConversationId(1L);
		dto.setContent("Hello");

		when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
		when(conversationService.getOrCreateConversation(dto)).thenReturn(conversation);
		when(translationConfigRepository.findAllByConversationIdAndUserId(any(), any())).thenReturn(Collections.emptyList());

		MessageModel messageWithEncryptedContent = new MessageModel();
		messageWithEncryptedContent.setContent("encryptedContent");
		when(messageRepository.save(any(MessageModel.class))).thenReturn(messageWithEncryptedContent);

		messageService.sendMessage(dto);

		verify(messageRepository, times(1)).save(any(MessageModel.class));
	}

	@Test
	void testSendMessageWithNonEmptySenderConfigs() throws Exception {
	    MessageCreateDTO dto = new MessageCreateDTO();
	    dto.setSenderId(1L);
	    dto.setReceiverId(2L);
	    dto.setConversationId(1L);
	    dto.setContent("Hello with sender config");
	    
	    TranslationConfigurationModel senderConfig = new TranslationConfigurationModel();
	    senderConfig.setType(TranslationType.IMPROVING);
	    senderConfig.setIsActive(true);

	    when(this.userRepository.findById(1L)).thenReturn(Optional.of(sender));
	    when(this.conversationService.getOrCreateConversation(dto)).thenReturn(conversation);
	    when(this.translationConfigRepository.findAllByConversationIdAndUserId(dto.getConversationId(), dto.getSenderId()))
	        .thenReturn(List.of(senderConfig));
	    when(this.translationConfigRepository.findAllByConversationIdAndUserId(dto.getConversationId(), dto.getReceiverId()))
	        .thenReturn(Collections.emptyList());

        when(this.geminiAPIService.validateContent(dto.getContent())).thenReturn("Mensagem Válida".trim());
	    when(this.geminiAPIService.improveText("Hello with sender config")).thenReturn("Improved Hello with sender config");
	    
	    this.messageService.sendMessage(dto);
	    
	    verify(this.messageRepository, times(1)).save(any(MessageModel.class));
	}

	@Test
	void testSendMessageWithNonEmptyReceiverConfigs() throws Exception {
	    MessageCreateDTO dto = new MessageCreateDTO();
	    dto.setSenderId(1L);
	    dto.setReceiverId(2L);
	    dto.setConversationId(1L);
	    dto.setContent("Hello with receiver config");

	    TranslationConfigurationModel receiverConfig = new TranslationConfigurationModel();
	    receiverConfig.setType(TranslationType.RECEIVING);
	    receiverConfig.setIsActive(true);
	    receiverConfig.setTargetLanguage("es");

	    when(this.userRepository.findById(1L)).thenReturn(Optional.of(sender));
	    when(this.conversationService.getOrCreateConversation(dto)).thenReturn(conversation);
	    when(this.translationConfigRepository.findAllByConversationIdAndUserId(dto.getConversationId(), dto.getSenderId()))
	        .thenReturn(Collections.emptyList());
	    when(this.translationConfigRepository.findAllByConversationIdAndUserId(dto.getConversationId(), dto.getReceiverId()))
	        .thenReturn(List.of(receiverConfig));

        when(this.geminiAPIService.validateContent(dto.getContent())).thenReturn("Mensagem Válida".trim());
	    when(this.geminiAPIService.translateText(anyString(), eq("es"))).thenReturn("Translated Hello with receiver config");

	    this.messageService.sendMessage(dto);

	    verify(this.messageRepository, times(1)).save(any(MessageModel.class));
	}

}
