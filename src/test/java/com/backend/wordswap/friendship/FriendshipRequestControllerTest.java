package com.backend.wordswap.friendship;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.backend.wordswap.friendshipRequest.FriendshipRequestController;
import com.backend.wordswap.friendshipRequest.FriendshipRequestService;
import com.backend.wordswap.friendshipRequest.dto.FriendshipDTO;
import com.backend.wordswap.friendshipRequest.entity.enumeration.StatusType;

public class FriendshipRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FriendshipRequestService friendshipRequestService;

    @InjectMocks
    private FriendshipRequestController friendshipRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(friendshipRequestController).build();
    }

    @Test
    void testFindAllByUserId() throws Exception {
        Long userId = 1L;
        FriendshipDTO dto1 = new FriendshipDTO(1L, "senderUser1", "receiverUser1", StatusType.PENDING, LocalDate.now().atStartOfDay());
        FriendshipDTO dto2 = new FriendshipDTO(2L, "senderUser2", "receiverUser2", StatusType.PENDING, LocalDate.now().atStartOfDay());
        when(this.friendshipRequestService.findAllByUserId(userId)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/friendship/find-pending-invites")
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender").value("senderUser1"))
                .andExpect(jsonPath("$[1].sender").value("senderUser2"));
    }
}
