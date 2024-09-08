package com.backend.wordswap.friendship.request;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.wordswap.friendship.dto.FriendshipDTO;
import com.backend.wordswap.friendship.request.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;

@RestController
@RequestMapping("/friendship")
public class FriendshipRequestController {

	private final FriendshipRequestService friendshipRequestService;

	public FriendshipRequestController(FriendshipRequestService friendshipRequestService) {
		this.friendshipRequestService = friendshipRequestService;
	}

//	@PostMapping(path = "/send-invite", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	public FriendshipDTO sendInvite(@RequestBody FriendshipRequestCreateDTO dto) {
//		return this.friendshipRequestService.sendInvite(dto);
//	}

	@GetMapping(path = "/find-pending-invites", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FriendshipDTO> findAllByUserId(@RequestParam("userId") Long userId) {
		return this.friendshipRequestService.findAllByUserId(userId);
	}

	@PutMapping(path = "/change-invite")
	public void changeInvite(@RequestParam("inviteId") Long inviteId, @RequestParam("status") StatusType statusType) {
		this.friendshipRequestService.changeStatus(inviteId, statusType);
	}

	@PutMapping(path = "/delete-friendship")
	public void deleteFriendship(@RequestParam("userId") Long userId, @RequestParam("friendId") Long friendId) {
		this.friendshipRequestService.deleteFriendship(userId, friendId);
	}
}
