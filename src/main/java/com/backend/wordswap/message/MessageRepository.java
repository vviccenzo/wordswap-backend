package com.backend.wordswap.message;

import com.backend.wordswap.message.entity.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageModel, Long> {
}
