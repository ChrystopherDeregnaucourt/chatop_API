package com.chatop.api.message.dto;

// Données attendues lorsqu'un utilisateur envoie un message à propos d'une location.

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageRequest(
        // Contenu du message ; ne doit pas être vide pour être utile au propriétaire.
        @NotBlank(message = "Message is required")
        String message,
        // Identifiant de l'utilisateur qui envoie le message.
        @NotNull(message = "User id is required")
        Long userId,
        // Identifiant de la location ciblée.
        @NotNull(message = "Rental id is required")
        Long rentalId
) {
}
