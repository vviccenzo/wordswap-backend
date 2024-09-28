package com.backend.wordswap.message;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.wordswap.message.entity.MessageModel;

public interface MessageRepository extends JpaRepository<MessageModel, Long> {

	List<MessageModel> findAllByConversationIdIn(Set<Long> conversationsId, Pageable pageable);

	@Query("SELECT m.conversation.id, COUNT(m) FROM MessageModel m WHERE m.conversation.id IN :conversationIds GROUP BY m.conversation.id")
	List<Object[]> findTotalMessagesByConversationIds(Set<Long> conversationIds);

}
