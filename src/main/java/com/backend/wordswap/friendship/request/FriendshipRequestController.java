package com.backend.wordswap.friendship.request;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.friendship.dto.FriendshipDTO;

@RestController
@RequestMapping("/friendship")
public class FriendshipRequestController {

	private final FriendshipRequestService friendshipRequestService;

	public FriendshipRequestController(FriendshipRequestService friendshipRequestService) {
		this.friendshipRequestService = friendshipRequestService;
	}

	@GetMapping(path = "/find-pending-invites", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FriendshipDTO> findAllByUserId(@RequestParam("userId") Long userId) {
		return this.friendshipRequestService.findAllByUserId(userId);
	}

}
