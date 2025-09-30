package com.chatop.api.auth.dto;

// Le package dto (Data Transfer Object) rassemble les structures de données utilisées pour communiquer
// entre le backend et le monde extérieur (contrôleurs, clients HTTP). Elles sont simples et sérialisables.

// Un record Java est une manière concise de déclarer un objet immuable destiné au transport de données.
// Ici, AuthResponse représente la réponse envoyée après une authentification réussie.
public record AuthResponse(
        // Le token JWT généré ; il sera utilisé par le client pour prouver son identité à chaque requête.
        String token,
        // Le type de token afin d'indiquer comment l'envoyer dans l'entête Authorization (par convention "Bearer").
        String tokenType,
        // La durée de validité du token exprimée en secondes, utile pour savoir quand le régénérer.
        long expiresIn
) {
    // Méthode utilitaire qui crée une réponse standardisée de type Bearer.
    // Cela évite de dupliquer la chaîne "Bearer" et réduit les risques d'erreur de frappe.
    public static AuthResponse bearer(String token, long expiresIn) {
        return new AuthResponse(token, "Bearer", expiresIn);
    }
}
