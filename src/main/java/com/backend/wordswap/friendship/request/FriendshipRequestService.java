package com.backend.wordswap.friendship.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
import com.backend.wordswap.friendship.dto.FriendshipRequestUpdateDTO;
import com.backend.wordswap.friendship.exception.FriendshipAlreadySendedException;
import com.backend.wordswap.friendship.request.dto.FriendshipDeleteRequestDTO;
import com.backend.wordswap.friendship.request.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.friendship.request.entity.FriendshipRequestModel;
import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.UserService;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserNotFoundException;
import com.backend.wordswap.websocket.WebSocketAction;
import com.backend.wordswap.websocket.WebSocketResponse;

import jakarta.transaction.Transactional;

@Service
public class FriendshipRequestService {

	private final SimpMessagingTemplate messagingTemplate;

	private final UserRepository userRepository;

	private final FriendshipRequestRepository friendshipRequestRepository;

	private final UserService userService;

	public FriendshipRequestService(UserRepository userRepository,
			FriendshipRequestRepository friendshipRequestRepository, SimpMessagingTemplate messagingTemplate,
			UserService userService) {
		this.userRepository = userRepository;
		this.friendshipRequestRepository = friendshipRequestRepository;
		this.messagingTemplate = messagingTemplate;
		this.userService = userService;
	}

	public void sendInvite(FriendshipRequestCreateDTO dto, WebSocketAction socketAction) {

		this.validateRequest(dto);

		FriendshipRequestModel sentModel = new FriendshipRequestModel();
		Optional<UserModel> optSender = this.userRepository.findById(dto.getSenderId());
		if (optSender.isEmpty()) {
			throw new UserNotFoundException("User sender not founded.");
		}

		Optional<UserModel> optTarget = this.userRepository.findByUserCode(dto.getTargetUserCode());

		if (optTarget.isEmpty()) {
			throw new UserNotFoundException("User with this code not exists.");
		}

		sentModel.setSender(optSender.get());
		sentModel.setReceiver(optTarget.get());
		sentModel.setStatus(StatusType.PENDING);
		sentModel.setRequestDate(LocalDateTime.now());
		sentModel = this.friendshipRequestRepository.save(sentModel);

		List<FriendshipDTO> requestsSender = this.findAllByUserId(dto.getSenderId());
		List<FriendshipDTO> requestsTarget = this.findAllByUserId(optTarget.get().getId());

		this.messagingTemplate.convertAndSend("/topic/messages/" + dto.getSenderId(),
				new WebSocketResponse<List<FriendshipDTO>>(socketAction, requestsSender));
		this.messagingTemplate.convertAndSend("/topic/messages/" + optTarget.get().getId(),
				new WebSocketResponse<List<FriendshipDTO>>(socketAction, requestsTarget));
	}

	private boolean validateRequest(FriendshipRequestCreateDTO dto) {
		Optional<FriendshipRequestModel> optRequest = this.friendshipRequestRepository
				.findBySenderIdAndTargetUserCode(dto.getSenderId(), dto.getTargetUserCode());
		if (optRequest.isPresent()) {
			throw new FriendshipAlreadySendedException("Friendship already sended.");
		}

		return Boolean.TRUE;
	}

	@Transactional
	public void changeStatus(FriendshipRequestUpdateDTO dto) {
		FriendshipRequestModel invite = this.friendshipRequestRepository.findById(dto.inviteId()).orElseThrow();
		invite.setStatus(dto.statusType());

		this.friendshipRequestRepository.save(invite);

		if (StatusType.ACCEPTED.equals(dto.statusType())) {
			UserModel user1 = this.userRepository.findById(invite.getSender().getId()).orElseThrow();
			UserModel user2 = this.userRepository.findById(invite.getReceiver().getId()).orElseThrow();

			user1.getFriends().add(user2);
			user2.getFriends().add(user1);

			this.userRepository.save(user1);
			this.userRepository.save(user2);

			List<UserDTO> friendsSender = this.userService.findFriendsByUserId(user1.getId());
			List<UserDTO> friendsTarget = this.userService.findFriendsByUserId(user2.getId());

			this.messagingTemplate.convertAndSend("/topic/messages/" + user1.getId(),
					new WebSocketResponse<List<UserDTO>>(WebSocketAction.ACCEPT_FRIEND_REQUEST, friendsSender));
			this.messagingTemplate.convertAndSend("/topic/messages/" + user2.getId(),
					new WebSocketResponse<List<UserDTO>>(WebSocketAction.ACCEPT_FRIEND_REQUEST, friendsTarget));
		}

		List<FriendshipDTO> requestsSender = this.findAllByUserId(invite.getSender().getId());
		List<FriendshipDTO> requestsTarget = this.findAllByUserId(invite.getReceiver().getId());

		this.messagingTemplate.convertAndSend("/topic/messages/" + invite.getSender().getId(),
				new WebSocketResponse<List<FriendshipDTO>>(WebSocketAction.UPDATE_FRIEND_REQUEST, requestsSender));
		this.messagingTemplate.convertAndSend("/topic/messages/" + invite.getReceiver().getId(),
				new WebSocketResponse<List<FriendshipDTO>>(WebSocketAction.UPDATE_FRIEND_REQUEST, requestsTarget));
	}

	@Transactional
	public void deleteFriendship(FriendshipDeleteRequestDTO dto) {
		UserModel user = this.userRepository.findById(dto.userId()).orElseThrow(() -> new UserNotFoundException("User not found."));

		boolean removed = user.getFriends().removeIf(friend -> friend.getId().equals(dto.friendId()));
		if (!removed) {
			throw new UserNotFoundException("Friendship not found.");
		}

		UserModel friend = this.userRepository.findById(dto.friendId()).orElseThrow(() -> new UserNotFoundException("Friend not found."));

		friend.getFriends().removeIf(friendToDelete -> friendToDelete.getId().equals(dto.userId()));

		this.userRepository.save(user);
		this.userRepository.save(friend);

		List<UserDTO> friendsSender = this.userService.findFriendsByUserId(friend.getId());
		List<UserDTO> friendsTarget = this.userService.findFriendsByUserId(user.getId());

		this.messagingTemplate.convertAndSend("/topic/messages/" + friend.getId(),
				new WebSocketResponse<List<UserDTO>>(WebSocketAction.ACCEPT_FRIEND_REQUEST, friendsSender));
		this.messagingTemplate.convertAndSend("/topic/messages/" + user.getId(),
				new WebSocketResponse<List<UserDTO>>(WebSocketAction.ACCEPT_FRIEND_REQUEST, friendsTarget));
	}

	public List<FriendshipDTO> findAllByUserId(Long userId) {
		return this.friendshipRequestRepository.findAllByReceiverIdAndStatus(userId, StatusType.PENDING).stream()
				.map(FriendshipRequestFactory::buildDTO).toList();
	}
}
