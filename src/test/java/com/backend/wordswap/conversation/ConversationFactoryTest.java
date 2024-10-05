package com.backend.wordswap.conversation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.backend.wordswap.conversation.dto.ConversationResponseDTO;
import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.conversation.factory.ConversationFactory;
import com.backend.wordswap.encrypt.Encrypt;
import com.backend.wordswap.message.dto.MessageRecord;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.dto.TranslationConfigResponseDTO;
import com.backend.wordswap.user.entity.UserModel;

public class ConversationFactoryTest {

	private UserModel userInitiator;
	private UserModel userRecipient;
	private ConversationModel conversation;
    private Map<Long, List<MessageModel>> messageByConversation;

    @BeforeEach
    void setUp() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        userInitiator = new UserModel();
        userInitiator.setId(1L);
        userInitiator.setName("Sender");
        
        userRecipient = new UserModel();
        userRecipient.setId(2L);
        userRecipient.setName("Receiver");

        conversation = new ConversationModel();
        conversation.setId(1L);
        conversation.setUserInitiator(userInitiator);
        conversation.setUserRecipient(userRecipient);
        conversation.setArchivedInitiator(false);
        conversation.setArchivedRecipient(false);
        this.conversation.setIsDeletedInitiator(false);
        this.conversation.setIsDeletedRecipient(false);

        userInitiator.getInitiatedConversations().add(conversation);
        userRecipient.getReceivedConversations().add(conversation);
        
        Mockito.mockStatic(Encrypt.class);
        Mockito.when(Encrypt.decrypt(Mockito.anyString())).thenReturn("Hello");

        messageByConversation = new HashMap<>();
    }

    @Test
    void testBuildConversationsResponse() {
        this.messageByConversation.put(1L, new ArrayList<>()); 
        List<ConversationResponseDTO> result = ConversationFactory.buildConversationsResponse(
                userInitiator, messageByConversation, userInitiator.getId(), Map.of()
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Receiver", result.get(0).getConversationName());
    }

    @Test
    void testBuildMessages() {
        MessageModel message = new MessageModel();
        message.setId(1L);
        message.setSender(userInitiator);
        message.setSentAt(LocalDateTime.now());

        String originalMessage = "Hello";
        String encodedMessage = Base64.getEncoder().encodeToString(originalMessage.getBytes());
        message.setContent(encodedMessage);

        this.messageByConversation.put(conversation.getId(), List.of(message));
        Map<Long, Long> totalMessagesByConversation = new HashMap<>();
        totalMessagesByConversation.put(1L, 1L);

        ConversationResponseDTO result = ConversationFactory.buildMessages(userInitiator.getId(), conversation, messageByConversation, totalMessagesByConversation);

        assertNotNull(result);
        assertEquals(conversation.getId(), result.getId());
        assertEquals("Receiver", result.getConversationName());
        assertEquals(1, result.getTotalMessages(), "Expected total messages to be 1");

        assertEquals(1, result.getUserMessages().size(), "Expected user messages size to be 1");
        assertEquals("Hello", result.getUserMessages().get(0).getContent(), "Expected message content to be 'Hello'");
    }

	@Test
	void testGetProfilePic() {
		String profilePic = ConversationFactory.getProfilePic(conversation, true);
		assertEquals("", profilePic);
	}

	@Test
	void testGetTranslationConfig() {
		TranslationConfigResponseDTO config = ConversationFactory.buildTranslationConfig(userInitiator.getId(), conversation);

		assertNotNull(config);
		assertFalse(config.getIsReceivingTranslation());
		assertFalse(config.getIsImprovingText());
	}

	@Test
	void testDetermineLastMessage() {
	    LocalDateTime now = LocalDateTime.now();
	    
	    MessageRecord userMessage = MessageRecord.builder()
	            .timeStamp(now.minusMinutes(1))
	            .content("User message content")
	            .build();
	    
	    MessageRecord targetMessage = MessageRecord.builder()
	            .timeStamp(now)
	            .content("Target message content")
	            .build();

	    List<MessageRecord> userMessages = List.of(userMessage);
	    List<MessageRecord> targetMessages = List.of(targetMessage);
	    
	    Map<LocalDateTime, String> lastMessage = ConversationFactory.determineLastMessage(userMessages, targetMessages);

	    assertNotNull(lastMessage);
	    assertEquals(1, lastMessage.size());
	    assertTrue(lastMessage.containsKey(now));
	    assertEquals("Target message content", lastMessage.get(now));
	}

}