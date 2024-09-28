package com.backend.wordswap.message.entity;

import java.time.LocalDateTime;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	@Column(name = "is_edited")
	private Boolean isEdited;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "is_translated")
	private Boolean isTranslated;

	public MessageModel(String content, UserModel sender, ConversationModel conversation) {
		this.content = content;
		this.sender = sender;
		this.sentAt = LocalDateTime.now();
		this.conversation = conversation;
	}
}
