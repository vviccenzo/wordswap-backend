package com.backend.wordswap.friendship;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
import com.backend.wordswap.friendship.exception.FriendshipAlreadySendedException;
import com.backend.wordswap.friendship.request.FriendshipRequestRepository;
import com.backend.wordswap.friendship.request.FriendshipRequestService;
import com.backend.wordswap.friendship.request.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.friendship.request.entity.FriendshipRequestModel;
import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserNotFoundException;

class FriendshipRequestServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private FriendshipRequestRepository friendshipRequestRepository;

	@InjectMocks
	private FriendshipRequestService friendshipRequestService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSendInviteSuccess() {
		FriendshipRequestCreateDTO dto = new FriendshipRequestCreateDTO();
		dto.setSenderId(1L);
		dto.setTargetUserCode("friendCode");

		UserModel sender = new UserModel();
		sender.setId(1L);

		UserModel receiver = new UserModel();
		receiver.setId(2L);

		when(this.userRepository.findById(dto.getSenderId())).thenReturn(Optional.of(sender));
		when(this.userRepository.findByUserCode(dto.getTargetUserCode())).thenReturn(Optional.of(receiver));
		when(this.friendshipRequestRepository.findBySenderIdAndTargetUserCode(dto.getSenderId(),
				dto.getTargetUserCode())).thenReturn(Optional.empty());

		FriendshipRequestModel savedRequest = new FriendshipRequestModel();
		savedRequest.setId(1L);
		savedRequest.setSender(sender);
		savedRequest.setReceiver(receiver);
		savedRequest.setStatus(StatusType.PENDING);
		savedRequest.setRequestDate(LocalDateTime.now());

		when(this.friendshipRequestRepository.save(any(FriendshipRequestModel.class))).thenReturn(savedRequest);

		FriendshipDTO result = this.friendshipRequestService.sendInvite(dto);

		assertNotNull(result);
		assertEquals(StatusType.PENDING, result.status());
		verify(this.friendshipRequestRepository, times(1)).save(any(FriendshipRequestModel.class));
	}

	@Test
	void testSendInviteUserNotFound() {
		FriendshipRequestCreateDTO dto = new FriendshipRequestCreateDTO();
		dto.setSenderId(1L);
		dto.setTargetUserCode("friendCode");

		when(this.userRepository.findById(dto.getSenderId())).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> this.friendshipRequestService.sendInvite(dto));
	}

	@Test
	void testSendInviteAlreadySended() {
		FriendshipRequestCreateDTO dto = new FriendshipRequestCreateDTO();
		dto.setSenderId(1L);
		dto.setTargetUserCode("friendCode");

		UserModel sender = new UserModel();
		sender.setId(1L);

		UserModel receiver = new UserModel();
		receiver.setId(2L);

		FriendshipRequestModel existingRequest = new FriendshipRequestModel();
		existingRequest.setSender(sender);
		existingRequest.setReceiver(receiver);

		when(this.userRepository.findById(dto.getSenderId())).thenReturn(Optional.of(sender));
		when(this.userRepository.findByUserCode(dto.getTargetUserCode())).thenReturn(Optional.of(receiver));
		when(this.friendshipRequestRepository.findBySenderIdAndTargetUserCode(dto.getSenderId(),
				dto.getTargetUserCode())).thenReturn(Optional.of(existingRequest));

		assertThrows(FriendshipAlreadySendedException.class, () -> this.friendshipRequestService.sendInvite(dto));
	}

	@Test
	void testChangeStatusSuccess() {
		Long inviteId = 1L;
		StatusType newStatus = StatusType.ACCEPTED;

		UserModel sender = new UserModel();
		sender.setId(1L);

		UserModel receiver = new UserModel();
		receiver.setId(2L);

		FriendshipRequestModel invite = new FriendshipRequestModel();
		invite.setId(inviteId);
		invite.setSender(sender);
		invite.setReceiver(receiver);
		invite.setStatus(StatusType.PENDING);

		when(this.friendshipRequestRepository.findById(inviteId)).thenReturn(Optional.of(invite));
		when(this.userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
		when(this.userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

		this.friendshipRequestService.changeStatus(inviteId, newStatus);

		assertEquals(newStatus, invite.getStatus());
		verify(this.friendshipRequestRepository, times(1)).save(invite);
		verify(this.userRepository, times(1)).save(sender);
		verify(this.userRepository, times(1)).save(receiver);
	}

	@Test
	void testDeleteFriendshipSuccess() {
		Long userId = 1L;
		Long friendId = 2L;

		UserModel user = new UserModel();
		user.setId(userId);

		UserModel friend = new UserModel();
		friend.setId(friendId);

		user.getFriends().add(friend);
		friend.getFriends().add(user);

		when(this.userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(this.userRepository.findById(friendId)).thenReturn(Optional.of(friend));

		this.friendshipRequestService.deleteFriendship(userId, friendId);

		assertTrue(user.getFriends().isEmpty());
		assertTrue(friend.getFriends().isEmpty());
		verify(this.userRepository, times(1)).save(user);
		verify(this.userRepository, times(1)).save(friend);
	}

	@Test
	void testDeleteFriendshipNotFound() {
		Long userId = 1L;
		Long friendId = 2L;

		UserModel user = new UserModel();
		user.setId(userId);

		when(this.userRepository.findById(userId)).thenReturn(Optional.of(user));

		assertThrows(UserNotFoundException.class,
				() -> this.friendshipRequestService.deleteFriendship(userId, friendId));
	}

	@Test
	void testFindAllByUserIdSuccess() {
		Long userId = 1L;
		UserModel sender = new UserModel();
		sender.setId(1L);

		UserModel receiver = new UserModel();
		receiver.setId(2L);

		FriendshipRequestModel request = new FriendshipRequestModel();
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setStatus(StatusType.PENDING);

		when(this.friendshipRequestRepository.findAllByReceiverIdAndStatus(userId, StatusType.PENDING))
				.thenReturn(List.of(request));

		List<FriendshipDTO> result = this.friendshipRequestService.findAllByUserId(userId);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(StatusType.PENDING, result.get(0).status());
	}
}