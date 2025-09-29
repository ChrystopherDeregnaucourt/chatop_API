package com.chatop.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "Login must be a valid email")
        @NotBlank(message = "Login is required")
        String login,
        @NotBlank(message = "Password is required")
        String password
) {
}
