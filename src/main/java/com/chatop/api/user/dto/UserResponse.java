package com.chatop.api.user.dto;

// Les DTO utilisateur exposent les informations que l'API fournit sans dévoiler les détails sensibles (mot de passe...).

import java.time.LocalDateTime;

// Record immuable retourné aux clients pour représenter un utilisateur.
public record UserResponse(
        // Identifiant unique en base de données.
        Long id,
        // Nom complet tel qu'il doit apparaître dans l'interface.
        String name,
        // Adresse email utilisée pour se connecter et communiquer.
        String email,
        // Date de création du compte ; utile pour des interfaces d'administration.
        LocalDateTime createdAt,
        // Date de dernière mise à jour pour suivre les modifications (profil, mot de passe...).
        LocalDateTime updatedAt
) {
}
