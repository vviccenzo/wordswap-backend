package com.backend.wordswap.message.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	@OneToOne(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private MessageImageModel image;

	@Column(name = "message_content", columnDefinition = "TEXT" )
	private String content;

	@Column(name = "message_content_original", columnDefinition = "TEXT" )
	private String contentOriginal;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	@Column(name = "viewed_at")
	private LocalDateTime viewedAt;

	@Column(name = "is_edited")
	private Boolean isEdited;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "is_translated")
	private Boolean isTranslated;

	@Column(name = "viewed")
	private Boolean viewed;

	public MessageModel(String content, UserModel sender, ConversationModel conversation) {
		this.content = content;
		this.sender = sender;
		this.sentAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
		this.conversation = conversation;
	}
}
