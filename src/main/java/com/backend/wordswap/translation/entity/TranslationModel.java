package com.backend.wordswap.translation.entity;

import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.message.entity.MessageModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
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
@Table(name = "translation")
@EqualsAndHashCode(callSuper = true)
public class TranslationModel extends GenericModel {

	@OneToOne(cascade = CascadeType.ALL)
	private MessageModel message;

	@Column(name = "language_code_sending")
	private String languageCodeSending;

	@Lob
	@Column(name = "content_sending_new")
	private String contentSending;

	@Column(name = "language_code_receiver")
	private String languageCodeReceiver;

	@Lob
	@Column(name = "content_receiver_new")
	private String contentReceiver;

}
