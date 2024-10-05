package com.backend.wordswap.message;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.backend.wordswap.message.dto.MessageCreateDTO;

class MessageCreateDTOTest {

	private MessageCreateDTO messageCreateDTO;

	@BeforeEach
	void setUp() {
		messageCreateDTO = new MessageCreateDTO();
	}

	@Test
	void testConstructorWithParameters() {
		Long senderId = 1L;
		String content = "Hello, World!";
		Long receiverId = 2L;

		messageCreateDTO = new MessageCreateDTO(senderId, content, receiverId);

		assertEquals(senderId, messageCreateDTO.getSenderId());
		assertEquals(content, messageCreateDTO.getContent());
		assertEquals(receiverId, messageCreateDTO.getReceiverId());
	}

	@Test
	void testGetImageBytes() {
		String base64Image = Base64.getEncoder().encodeToString("image content".getBytes());
		messageCreateDTO.setImageContent(base64Image);

		byte[] imageBytes = messageCreateDTO.getImageBytes();

		assertArrayEquals("image content".getBytes(), imageBytes);
	}

	@Test
	void testDefaultValues() {
		assertNotNull(messageCreateDTO.getConversationId());
		assertEquals(0L, messageCreateDTO.getConversationId());
		assertEquals(0, messageCreateDTO.getPageNumber());
	}
}
