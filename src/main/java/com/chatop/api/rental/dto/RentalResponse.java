package com.chatop.api.rental.dto;

// DTO retourné par l'API pour décrire une annonce de location.

import java.time.LocalDateTime;

public record RentalResponse(
        // Identifiant unique de l'annonce.
        Long id,
        // Titre descriptif visible par les utilisateurs.
        String name,
        // Surface en m².
        Integer surface,
        // Prix de location.
        Integer price,
        // Description textuelle.
        String description,
        // URL publique de l'image associée à la location.
        String pictureUrl,
        // Identifiant du propriétaire (référence à l'utilisateur).
        Long ownerId,
        // Nom du propriétaire pour éviter un aller-retour supplémentaire côté client.
        String ownerName,
        // Dates de création et de mise à jour pour le suivi.
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
