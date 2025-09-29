package com.chatop.api.message.dto;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String message,
        Long rentalId,
        Long userId,
        LocalDateTime createdAt
) {
}
