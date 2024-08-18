package com.backend.wordswap.conversation;

import com.backend.wordswap.conversation.entity.ConversationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<ConversationModel, Long> {
}
