package com.backend.wordswap.conversation.entity;

import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversation")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ConversationModel extends GenericModel {

	@ManyToOne
	@JoinColumn(name = "initiator")
	private UserModel userInitiator;

	@ManyToOne
	@JoinColumn(name = "recipient")
	private UserModel userRecipient;

	@OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MessageModel> messages = new ArrayList<>();

	@OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TranslationConfigurationModel> translationConfigurations = new ArrayList<>();

	@Column(name = "created_date")
	private LocalDate createdDate;

	@Column(name = "is_deleted_initiator")
	private Boolean isDeletedInitiator = Boolean.FALSE;

	@Column(name = "is_deleted_recipient")
	private Boolean isDeletedRecipient = Boolean.FALSE;

	@Column(name = "is_archived_initiator", nullable = false)
	private boolean isArchivedInitiator;

	@Column(name = "is_archived_recipient")
	private boolean isArchivedRecipient;

	@Transient
	public TranslationConfigurationModel getTranslationByUserId(Long userId) {
		return translationConfigurations.stream().filter(
				translationConfigurationModel -> translationConfigurationModel.getUser().getId().compareTo(userId) == 0)
				.findFirst().orElse(null);
	}

	@Transient
	public List<MessageModel> getMessages() {
		return this.messages;
	}
}
