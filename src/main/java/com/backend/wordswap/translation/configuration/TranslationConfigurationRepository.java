package com.backend.wordswap.translation.configuration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.backend.wordswap.translation.configuration.entity.TranslationConfigurationModel;

public interface TranslationConfigurationRepository extends JpaRepository<TranslationConfigurationModel, Long> {

	List<TranslationConfigurationModel> findAllByConversationId(Long conversationId);

	@Modifying
	void deleteAllByUserIdAndConversationId(Long userId, Long conversationId);

}
