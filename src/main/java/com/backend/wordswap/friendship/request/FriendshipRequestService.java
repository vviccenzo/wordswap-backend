package com.backend.wordswap.friendship.request;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
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
}
