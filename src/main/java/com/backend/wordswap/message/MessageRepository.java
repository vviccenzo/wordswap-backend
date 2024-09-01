package com.backend.wordswap.message;

import com.backend.wordswap.message.entity.MessageModel;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageModel, Long> {

	List<MessageModel> findAllByConversationIdIn(Set<Long> conversationsId, Pageable pageable);

}
