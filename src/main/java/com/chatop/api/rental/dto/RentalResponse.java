package com.chatop.api.rental.dto;

import java.time.LocalDateTime;

public record RentalResponse(
        Long id,
        String name,
        Integer surface,
        Integer price,
        String description,
        String pictureUrl,
        Long ownerId,
        String ownerName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
