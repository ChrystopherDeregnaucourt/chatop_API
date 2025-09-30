package com.chatop.api.common.dto;

// Les DTO communs servent à standardiser la manière dont l'API répond, quel que soit le domaine métier.

import java.time.OffsetDateTime;
import java.util.Map;

// Record représentant une erreur structurée que l'API peut retourner.
// Structurer les erreurs facilite leur interprétation côté client et simplifie le débogage.
public record ApiErrorResponse(
        // Date et heure précises de l'erreur (avec fuseau horaire) pour faciliter la corrélation dans les logs.
        OffsetDateTime timestamp,
        // Code HTTP renvoyé (ex: 400, 404, 500) afin que le client sache quelle catégorie d'erreur est survenue.
        int status,
        // Intitulé court décrivant le type d'erreur (ex: "Bad Request").
        String error,
        // Message plus détaillé permettant de comprendre le problème.
        String message,
        // L'URL qui a provoqué l'erreur pour aider à la reproduction.
        String path,
        // Détails supplémentaires optionnels (ex: erreurs de validation par champ).
        Map<String, Object> details
) {
    // Fabrique une réponse d'erreur en fixant automatiquement la date de génération.
    // Cette méthode utilitaire garantit l'homogénéité des erreurs créées dans l'application.
    public static ApiErrorResponse of(int status, String error, String message, String path, Map<String, Object> details) {
        return new ApiErrorResponse(OffsetDateTime.now(), status, error, message, path, details);
    }
}
