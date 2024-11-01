package com.backend.wordswap.friendshipRequest.entity;

import java.time.LocalDateTime;

import com.backend.wordswap.friendshipRequest.entity.enumeration.StatusType;
import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
