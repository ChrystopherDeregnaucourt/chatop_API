package com.chatop.api.rental.mapper;

import com.chatop.api.rental.dto.RentalResponse;
import com.chatop.api.rental.model.Rental;
import com.chatop.api.storage.FileStorageService;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    private final FileStorageService fileStorageService;

    public RentalMapper(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

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
