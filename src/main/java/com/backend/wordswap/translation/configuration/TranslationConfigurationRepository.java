package com.backend.wordswap.translation.configuration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;

public interface TranslationConfigurationRepository extends JpaRepository<TranslationConfigurationModel, Long> {

	List<TranslationConfigurationModel> findAllByConversationId(Long conversationId);

}
