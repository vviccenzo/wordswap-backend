package com.backend.wordswap.translation.configuration.entity;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.translation.configuration.enumeration.TranslationType;
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
@EqualsAndHashCode(callSuper = true)
@Table(name = "translation_configuration")
public class TranslationConfigurationModel extends GenericModel {

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserModel user;

	@ManyToOne
	@JoinColumn(name = "conversation_id")
	private ConversationModel conversation;

	@Column(name = "target_language", nullable = true)
	private String targetLanguage;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "translation_type")
	private TranslationType type;

}
