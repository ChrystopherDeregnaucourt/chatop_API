package com.chatop.api.user.mapper;

// Les "mappers" MapStruct convertissent automatiquement les entités JPA en DTO et inversement.

import com.chatop.api.user.dto.UserResponse;
import com.chatop.api.user.model.User;
import org.mapstruct.Mapper;

// L'annotation @Mapper demande à MapStruct de générer une implémentation concrète de cette interface.
// componentModel = "spring" permet à Spring d'enregistrer automatiquement le mapper comme bean injectable.
@Mapper(componentModel = "spring")
public interface UserMapper {
    // Méthode de conversion : MapStruct générera le code qui copie champ à champ les données utiles.
    UserResponse toResponse(User user);
}
