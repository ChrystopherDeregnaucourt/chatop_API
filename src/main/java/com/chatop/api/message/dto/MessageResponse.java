package com.chatop.api.message.dto;

// DTO exposant les informations d'un message envoyé au propriétaire d'une location.

import java.time.LocalDateTime;

public record MessageResponse(
        // Identifiant du message.
        Long id,
        // Contenu textuel envoyé par l'utilisateur.
        String message,
        // Référence vers l'annonce concernée.
        Long rentalId,
        // Identifiant de l'expéditeur.
        Long userId,
        // Date d'envoi.
        LocalDateTime createdAt
) {
}
