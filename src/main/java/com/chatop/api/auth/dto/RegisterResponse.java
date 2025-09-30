package com.chatop.api.auth.dto;

// Ce package contient les objets d'échange spécifiquement liés aux opérations d'authentification.

import com.chatop.api.user.dto.UserResponse;

// Ce record combine l'utilisateur nouvellement créé avec les informations d'authentification retournées.
// Il permet au client de disposer immédiatement des données d'identité et du token JWT dans une réponse unique.
public record RegisterResponse(UserResponse user, AuthResponse auth) {
}
