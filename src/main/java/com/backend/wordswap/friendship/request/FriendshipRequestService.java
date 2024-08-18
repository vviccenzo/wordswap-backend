package com.backend.wordswap.friendship.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
import com.backend.wordswap.friendship.exception.FriendshipAlreadySendedException;
import com.backend.wordswap.friendship.request.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.friendship.request.entity.FriendshipRequestModel;
import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;
import com.backend.wordswap.user.UserRepository;
import com.backend.wordswap.user.entity.UserModel;
import com.backend.wordswap.user.exception.UserNotFoundException;

@Service
public class FriendshipRequestService {

	private final UserRepository userRepository;

	private final FriendshipRequestRepository friendshipRequestRepository;

	public FriendshipRequestService(UserRepository userRepository,
			FriendshipRequestRepository friendshipRequestRepository) {
		this.userRepository = userRepository;
		this.friendshipRequestRepository = friendshipRequestRepository;
	}

	public FriendshipDTO sendInvite(FriendshipRequestCreateDTO dto) {

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

		return FriendshipRequestFactory.buildDTO(sentModel);
	}

	private boolean validateRequest(FriendshipRequestCreateDTO dto) {
		Optional<FriendshipRequestModel> optRequest = this.friendshipRequestRepository
				.findBySenderIdAndTargetUserCode(dto.getSenderId(), dto.getTargetUserCode());
		if (optRequest.isPresent()) {
			throw new FriendshipAlreadySendedException("Friendship already sended.");
		}

		return Boolean.TRUE;
	}

	public void changeStatus(Long inviteId, StatusType statusType) {
		FriendshipRequestModel invite = this.friendshipRequestRepository.findById(inviteId).orElseThrow();
		invite.setStatus(statusType);

		this.friendshipRequestRepository.save(invite);

		if (StatusType.ACCEPTED.equals(statusType)) {
			UserModel user1 = this.userRepository.findById(invite.getSender().getId()).orElseThrow();
			UserModel user2 = this.userRepository.findById(invite.getReceiver().getId()).orElseThrow();

			user1.getFriends().add(user2);
			user2.getFriends().add(user1);

			this.userRepository.save(user1);
			this.userRepository.save(user2);
		}
	}

	public void deleteFriendship(Long userId, Long friendId) {
		Optional<UserModel> model = this.userRepository.findById(userId);
		if (model.isPresent()) {
			UserModel modelToUpdate = model.get();
			modelToUpdate.getFriends().stream().filter(friend -> friend.getId().compareTo(friendId) == 0)
					.forEach(friend -> {
						friend.getFriends().removeIf(friendToDelete -> friendToDelete.getId().compareTo(userId) == 0);

						this.userRepository.save(friend);
					});

			this.userRepository.save(modelToUpdate);
		}

		throw new UserNotFoundException("User not founded.");
	}

	public List<FriendshipDTO> findAllByUserId(Long userId) {
		return this.friendshipRequestRepository.findAllByReceiverIdAndStatus(userId, StatusType.PENDING).stream()
				.map(FriendshipRequestFactory::buildDTO).toList();
	}
}
