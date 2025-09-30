package com.chatop.api.auth.dto;

// Les requêtes de création de compte sont décrites ici pour clarifier les données attendues lors de l'inscription.

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Record immuable décrivant les informations minimales nécessaires pour créer un utilisateur.
public record RegisterRequest(
        // Adresse email valide qui servira d'identifiant de connexion et de contact.
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,
        // Nom affiché pour l'utilisateur ; on limite la longueur pour protéger la base de données et l'interface.
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,
        // Le mot de passe doit être présent et suffisamment long pour offrir un niveau de sécurité minimum.
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password
) {
}
