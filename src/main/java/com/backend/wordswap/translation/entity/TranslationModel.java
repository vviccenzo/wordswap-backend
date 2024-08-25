package com.backend.wordswap.translation.entity;

import com.backend.wordswap.generic.entity.GenericModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	@Column(name = "language_code_base")
	private String languageCodeBase;

	@Column(name = "content_base")
	private String contentBase;

	@Column(name = "language_code_target")
	private String languageCodeTarget;

	@Column(name = "content_translated")
	private String contentTranslated;

}
