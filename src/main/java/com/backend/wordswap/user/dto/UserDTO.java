package com.backend.wordswap.user.dto;

import java.time.LocalDate;

public record UserDTO(Long id, String label, LocalDate createdDate, Long conversationId) {
}
