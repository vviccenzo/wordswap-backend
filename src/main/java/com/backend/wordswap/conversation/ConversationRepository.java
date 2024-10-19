package com.backend.wordswap.conversation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.backend.wordswap.conversation.entity.ConversationModel;

import jakarta.transaction.Transactional;

public interface ConversationRepository extends JpaRepository<ConversationModel, Long> {

	public Optional<ConversationModel> findByUserInitiatorIdAndUserRecipientId(Long senderId, Long receiverId);

	@Modifying
	@Transactional
	@Query("""
			UPDATE ConversationModel c
			                   SET c.isDeletedInitiator = TRUE,
			                   c.isDeletedRecipient = TRUE
			                   WHERE (c.userInitiator.id = :id1 AND c.userRecipient.id = :id2)
			                   OR (c.userInitiator.id = :id2 AND c.userRecipient.id = :id1)
						""")
	public void deleteAllByFriendship(Long id1, Long id2);
}
