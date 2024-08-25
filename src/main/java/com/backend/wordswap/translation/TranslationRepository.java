package com.backend.wordswap.translation;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.wordswap.translation.entity.TranslationModel;

public interface TranslationRepository extends JpaRepository<TranslationModel, Long> {

}
