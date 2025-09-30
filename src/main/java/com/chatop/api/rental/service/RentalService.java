package com.chatop.api.rental.service;

// Service métier encapsulant les règles de gestion des locations.

import com.chatop.api.common.dto.PageResponse;
import com.chatop.api.common.exception.BadRequestException;
import com.chatop.api.common.exception.ResourceNotFoundException;
import com.chatop.api.rental.dto.RentalRequest;
import com.chatop.api.rental.dto.RentalResponse;
import com.chatop.api.rental.mapper.RentalMapper;
import com.chatop.api.rental.model.Rental;
import com.chatop.api.rental.repository.RentalRepository;
import com.chatop.api.storage.FileStorageService;
import com.chatop.api.user.UserService;
import com.chatop.api.user.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

// @Service signale à Spring que cette classe contient la logique métier.
@Service
public class RentalService {

    // Accès aux données des locations.
    private final RentalRepository rentalRepository;
    // Permet de récupérer les utilisateurs (propriétaires).
    private final UserService userService;
    // Gestion du stockage des photos.
    private final FileStorageService fileStorageService;
    // Conversion entité -> DTO.
    private final RentalMapper rentalMapper;

    public RentalService(RentalRepository rentalRepository,
                         UserService userService,
                         FileStorageService fileStorageService,
                         RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.rentalMapper = rentalMapper;
    }

    // Lecture paginée des locations. readOnly = true optimise les transactions pour les opérations de lecture.
    @Transactional(readOnly = true)
    public PageResponse<RentalResponse> list(Pageable pageable) {
        Page<Rental> rentals = rentalRepository.findAll(pageable);
        List<RentalResponse> content = rentals.getContent().stream()
                .map(rentalMapper::toResponse)
                .toList();
        return new PageResponse<>(content, rentals.getNumber(), rentals.getSize(), rentals.getTotalElements(), rentals.getTotalPages());
    }

    // Récupère une annonce précise ou lève une erreur 404 si elle n'existe pas.
    @Transactional(readOnly = true)
    public RentalResponse getById(Long id) {
        return rentalMapper.toResponse(getEntity(id));
    }

    // Crée une nouvelle location pour un propriétaire identifié par son email.
    @Transactional
    public RentalResponse create(RentalRequest request, String ownerEmail) {
        if (request.getPicture() == null || request.getPicture().isEmpty()) {
            throw new BadRequestException("Picture is required");
        }
        User owner = userService.getByEmail(ownerEmail);
        String storedFile = fileStorageService.store(request.getPicture());
        Rental rental = Rental.builder()
                .name(request.getName())
                .surface(request.getSurface())
                .price(request.getPrice())
                .description(request.getDescription())
                .picturePath(storedFile)
                .owner(owner)
                .build();
        rentalRepository.save(rental);
        return rentalMapper.toResponse(rental);
    }

    // Met à jour une location existante après vérification des droits du propriétaire.
    @Transactional
    public RentalResponse update(Long id, RentalRequest request, String ownerEmail) {
        Rental rental = getEntity(id);
        User owner = userService.getByEmail(ownerEmail);
        if (!rental.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You are not allowed to update this rental");
        }
        rental.setName(request.getName());
        rental.setSurface(request.getSurface());
        rental.setPrice(request.getPrice());
        if (StringUtils.hasText(request.getDescription()) || request.getDescription() == null) {
            rental.setDescription(request.getDescription());
        }
        if (request.getPicture() != null && !request.getPicture().isEmpty()) {
            String oldPicture = rental.getPicturePath();
            String stored = fileStorageService.store(request.getPicture());
            rental.setPicturePath(stored);
            fileStorageService.delete(oldPicture);
        }
        return rentalMapper.toResponse(rental);
    }

    // Méthode utilitaire réutilisable pour charger une entité Rental.
    @Transactional(readOnly = true)
    public Rental getEntity(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
    }
}
