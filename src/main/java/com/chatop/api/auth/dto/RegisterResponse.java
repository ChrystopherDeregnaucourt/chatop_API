package com.chatop.api.auth.dto;

import com.chatop.api.user.dto.UserResponse;

public record RegisterResponse(UserResponse user, AuthResponse auth) {
}
