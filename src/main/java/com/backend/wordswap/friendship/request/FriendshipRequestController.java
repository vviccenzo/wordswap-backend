package com.backend.wordswap.friendship.request;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
import com.backend.wordswap.friendship.request.dto.FriendshipRequestCreateDTO;

@RestController
@RequestMapping("/friendship")
public class FriendshipRequestController {

	private final FriendshipRequestService friendshipRequestService;

	public FriendshipRequestController(FriendshipRequestService friendshipRequestService) {
		this.friendshipRequestService = friendshipRequestService;
	}

	@PostMapping(path = "/send-invite", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public FriendshipDTO sendInvite(@RequestBody FriendshipRequestCreateDTO dto) {
		return this.friendshipRequestService.sendInvite(dto);
	}

}
