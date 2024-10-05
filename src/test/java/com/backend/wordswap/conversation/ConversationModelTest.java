package com.backend.wordswap.conversation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.user.entity.UserModel;

class ConversationModelTest {

	private ConversationModel conversation;

	@BeforeEach
	void setUp() {
		this.conversation = new ConversationModel();
		this.conversation.setUserInitiator(new UserModel());
		this.conversation.setUserRecipient(new UserModel());
		this.conversation.setMessages(new ArrayList<>());
		this.conversation.setTranslationConfigurations(new ArrayList<>());
		this.conversation.setCreatedDate(LocalDate.now());
	}

	@Test
	void testGetTranslationByUserId_ReturnsNull_WhenNoTranslationExists() {
		Long userId = 1L;
		TranslationConfigurationModel result = this.conversation.getTranslationByUserId(userId);
		assertNull(result);
	}

	@Test
	void testGetTranslationByUserId_ReturnsTranslation_WhenExists() {
		Long userId = 1L;

		UserModel user = new UserModel();
		user.setId(userId);

		TranslationConfigurationModel translation = new TranslationConfigurationModel();
		translation.setUser(user);
		this.conversation.getTranslationConfigurations().add(translation);

		TranslationConfigurationModel result = this.conversation.getTranslationByUserId(userId);

		assertNotNull(result);
		assertEquals(userId, result.getUser().getId());
	}

	@Test
	void testGetMessages_ReturnsMessagesList() {
		MessageModel message1 = new MessageModel();
		MessageModel message2 = new MessageModel();

		this.conversation.getMessages().add(message1);
		this.conversation.getMessages().add(message2);

		List<MessageModel> messages = conversation.getMessages();

		assertNotNull(messages);
		assertEquals(2, messages.size());
		assertTrue(messages.contains(message1));
		assertTrue(messages.contains(message2));
	}

}