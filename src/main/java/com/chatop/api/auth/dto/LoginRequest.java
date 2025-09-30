package com.chatop.api.auth.dto;

// Les DTO d'authentification sont placés dans ce package pour clarifier leur responsabilité.

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Ce record représente le corps JSON attendu lorsqu'un utilisateur tente de se connecter.
// Les annotations de validation Jakarta Bean Validation garantissent que les données minimales sont présentes.
public record LoginRequest(
        // @Email vérifie le format de l'adresse, ce qui évite de lancer l'authentification pour une saisie invalide.
        @Email(message = "Login must be a valid email")
        // @NotBlank garantit qu'une valeur non vide est fournie ; sinon, une erreur 400 est renvoyée.
        @NotBlank(message = "Login is required")
        String login,
        // Le mot de passe ne peut pas être vide ; cela évite de transmettre une requête sans informations d'identification.
        @NotBlank(message = "Password is required")
        String password
) {
}
