package com.chatop.api.rental.mapper;

// Composant chargé de transformer l'entité Rental en DTO exposable.

import com.chatop.api.rental.dto.RentalResponse;
import com.chatop.api.rental.model.Rental;
import com.chatop.api.storage.FileStorageService;
import org.springframework.stereotype.Component;

// @Component permet de l'injecter là où l'on en a besoin (services, contrôleurs...).
@Component
public class RentalMapper {

    // Service nécessaire pour générer l'URL publique de la photo.
    private final FileStorageService fileStorageService;

    public RentalMapper(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // Convertit une entité JPA Rental en RentalResponse prêt à être renvoyé par l'API.
    public RentalResponse toResponse(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getName(),
                rental.getSurface(),
                rental.getPrice(),
                rental.getDescription(),
                fileStorageService.buildPublicUrl(rental.getPicturePath()),
                rental.getOwner() != null ? rental.getOwner().getId() : null,
                rental.getOwner() != null ? rental.getOwner().getName() : null,
                rental.getCreatedAt(),
                rental.getUpdatedAt()
        );
    }
}
