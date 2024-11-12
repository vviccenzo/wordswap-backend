package com.backend.wordswap.conversation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.wordswap.conversation.entity.ConversationModel;

public interface ConversationRepository extends JpaRepository<ConversationModel, Long> {

	public Optional<ConversationModel> findByConversationCode(String conversationCode);

//	@Modifying
//	@Transactional
//	@Query("""
//	        UPDATE ConversationModel c
//	        SET c.isDeletedUser1 = TRUE,
//	            c.isDeletedUser2 = TRUE
//	        WHERE (c.user1.id = :userId1 AND c.user2.id = :userId2)
//	           OR (c.user1.id = :userId2 AND c.user2.id = :userId1)
//	       """)
//	void deleteAllByUsers(Long userId1, Long userId2);

}
