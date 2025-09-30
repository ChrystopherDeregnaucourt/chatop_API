package com.chatop.api.common.dto;

// DTO générique utilisé pour renvoyer des résultats paginés depuis n'importe quel endpoint.

import java.util.List;

// Grâce au type générique <T>, ce record peut contenir des listes d'objets de nature différente (utilisateurs, locations, etc.).
public record PageResponse<T>(
        // La liste des éléments réellement retournés pour la page demandée.
        List<T> content,
        // Numéro de la page actuelle (souvent basé sur 0 dans Spring Data).
        int page,
        // Nombre d'éléments par page, utile pour reconstituer la pagination côté client.
        int size,
        // Nombre total d'éléments disponibles toutes pages confondues.
        long totalElements,
        // Nombre total de pages ; peut servir pour afficher une pagination complète.
        int totalPages
) {
}
