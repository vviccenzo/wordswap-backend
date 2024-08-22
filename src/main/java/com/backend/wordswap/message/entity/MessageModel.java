package com.backend.wordswap.message.entity;

import com.backend.wordswap.conversation.entity.ConversationModel;
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
@Table(name = "message")
@EqualsAndHashCode(callSuper = true)
public class MessageModel extends GenericModel {

	@ManyToOne
	@JoinColumn(name = "conversation_id")
	private ConversationModel conversation;

	@ManyToOne
	@JoinColumn(name = "sender_id")
	private UserModel sender;

	@Column(name = "content")
	private String content;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	public MessageModel(String content, UserModel sender, ConversationModel conversation) {
		this.content = content;
		this.sender = sender;
		this.sentAt = LocalDateTime.now();
		this.conversation = conversation;
	}
}
