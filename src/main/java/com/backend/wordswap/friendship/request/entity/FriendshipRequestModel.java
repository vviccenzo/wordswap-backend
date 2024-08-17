package com.backend.wordswap.friendship.request.entity;

import com.backend.wordswap.friendship.request.entity.enumeration.StatusType;
import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "friendship_requests")
public class FriendshipRequestModel extends GenericModel {

	@ManyToOne
	@JoinColumn(name = "sender_id")
	private UserModel sender;

	@ManyToOne
	@JoinColumn(name = "receiver_id")
	private UserModel receiver;

	@Column(name = "request_date")
	private LocalDateTime requestDate;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private StatusType status;

}
