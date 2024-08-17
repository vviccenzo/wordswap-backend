package com.backend.wordswap.friendship.request;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.wordswap.friendship.request.entity.FriendshipRequestModel;

public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequestModel, Long> {

}
