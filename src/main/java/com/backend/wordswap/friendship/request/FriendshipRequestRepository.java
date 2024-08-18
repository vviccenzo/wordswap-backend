package com.backend.wordswap.friendship.request;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.wordswap.friendship.request.entity.FriendshipRequestModel;
import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequestModel, Long> {

	@Query("""
			SELECT f FROM FriendshipRequestModel f WHERE f.sender.id = :senderId AND f.receiver.userCode = :targetUserCode
			""")
	public Optional<FriendshipRequestModel> findBySenderIdAndTargetUserCode(@RequestParam("senderId") Long senderId,
			@RequestParam("targetUserCode") String targetUserCode);

	public List<FriendshipRequestModel> findAllByReceiverIdAndStatus(Long userId, StatusType status);
}
