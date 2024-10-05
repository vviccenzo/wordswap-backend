package com.backend.wordswap.friendship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
import com.backend.wordswap.friendship.request.entity.FriendshipRequestModel;
import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;
import com.backend.wordswap.user.entity.UserModel;

public class FriendshipFactoryTest {

	private FriendshipRequestModel friendshipRequestModel;

	@BeforeEach
	void setUp() {
		friendshipRequestModel = mock(FriendshipRequestModel.class);
		UserModel sender = mock(UserModel.class);
		UserModel receiver = mock(UserModel.class);

		when(sender.getUsername()).thenReturn("senderUser");
		when(receiver.getUsername()).thenReturn("receiverUser");
		when(friendshipRequestModel.getId()).thenReturn(1L);
		when(friendshipRequestModel.getSender()).thenReturn(sender);
		when(friendshipRequestModel.getReceiver()).thenReturn(receiver);
		when(friendshipRequestModel.getStatus()).thenReturn(StatusType.PENDING);
		when(friendshipRequestModel.getRequestDate()).thenReturn(LocalDate.now().atStartOfDay());
	}

	@Test
	void testBuildDTO() {
		FriendshipDTO dto = FriendshipFactory.buildDTO(friendshipRequestModel);

		assertEquals(1L, dto.id());
		assertEquals("senderUser", dto.sender());
		assertEquals("receiverUser", dto.receiver());
		assertEquals(StatusType.PENDING, dto.status());
		assertNotNull(dto.creationDate());
	}

}
