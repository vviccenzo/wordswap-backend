package com.backend.wordswap.translation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.wordswap.translation.entity.TranslationModel;

public interface TranslationRepository extends JpaRepository<TranslationModel, Long> {

	List<TranslationModel> findAllByLanguageCodeBaseAndLanguageCodeTarget(String baseLanguage, String targetLanguage);

}
