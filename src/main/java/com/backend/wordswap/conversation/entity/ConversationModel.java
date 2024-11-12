package com.backend.wordswap.conversation.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.backend.wordswap.conversation.profile.entity.ConversationProfileModel;
import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversation")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ConversationModel extends GenericModel {

	@Column(name = "conversation_code")
	private String conversationCode;

	@Column(name = "conversation_name")
	private String conversationName;

	@Column(name = "conversation_bio")
	private String conversationBio;

	@OneToOne(mappedBy = "conversation", orphanRemoval = true)
	private ConversationProfileModel conversationProfile;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "conversation_participants", joinColumns = @JoinColumn(name = "conversation_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<UserModel> participants = new ArrayList<>();

	@OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MessageModel> messages = new ArrayList<>();

	@OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<TranslationConfigurationModel> translationConfigurations = new ArrayList<>();

	@Column(name = "created_date")
	private LocalDate createdDate;

	@Column(name = "conversation_type")
	private ConversationType type;

	@Transient
	public TranslationConfigurationModel getTranslationByUserId(Long userId) {
		return translationConfigurations.stream()
				.filter(translationConfig -> translationConfig.getUser().getId().equals(userId)).findFirst()
				.orElse(null);
	}

	@Transient
	public Set<Long> getParticipantsIds() {
		return getParticipants().stream().map(UserModel::getId).collect(Collectors.toSet());
	}
}
