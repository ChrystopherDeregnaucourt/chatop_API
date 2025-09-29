package com.chatop.api.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageRequest(
        @NotBlank(message = "Message is required")
        String message,
        @NotNull(message = "User id is required")
        Long userId,
        @NotNull(message = "Rental id is required")
        Long rentalId
) {
}
