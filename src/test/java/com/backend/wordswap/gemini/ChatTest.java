package com.backend.wordswap.gemini;

import com.backend.wordswap.chat.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChatTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testChatResponseSerialization() throws Exception {
        String json = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "Hello, how can I help you?"
                      }
                    ]
                  }
                }
              ]
            }
        """;

        ChatResponse chatResponse = objectMapper.readValue(json, ChatResponse.class);

        assertNotNull(chatResponse);
        assertNotNull(chatResponse.getCandidates());
        assertEquals(1, chatResponse.getCandidates().length);
        assertNotNull(chatResponse.getCandidates()[0].getContent());
        assertNotNull(chatResponse.getCandidates()[0].getContent().getParts());
        assertEquals(1, chatResponse.getCandidates()[0].getContent().getParts().length);
        assertEquals("Hello, how can I help you?", chatResponse.getCandidates()[0].getContent().getParts()[0].getText());
    }

    @Test
    void testSettersAndGetters() {
        ChatResponse chatResponse = new ChatResponse();
        ChatResponse.Candidates candidates = new ChatResponse.Candidates();
        ChatResponse.Content content = new ChatResponse.Content();
        ChatResponse.Part part = new ChatResponse.Part();

        part.setText("Sample text");
        content.setParts(new ChatResponse.Part[]{part});
        candidates.setContent(content);
        chatResponse.setCandidates(new ChatResponse.Candidates[]{candidates});

        assertEquals(1, chatResponse.getCandidates().length);
        assertEquals("Sample text", chatResponse.getCandidates()[0].getContent().getParts()[0].getText());
    }
}
