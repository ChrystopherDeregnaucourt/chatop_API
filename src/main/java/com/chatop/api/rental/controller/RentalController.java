package com.chatop.api.rental.controller;

// Contrôleur REST exposant les endpoints liés aux annonces de location.

import com.chatop.api.common.dto.PageResponse;
import com.chatop.api.rental.dto.RentalRequest;
import com.chatop.api.rental.dto.RentalResponse;
import com.chatop.api.rental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

// @RestController + @RequestMapping définissent la base des routes.
@RestController
@RequestMapping("/api/rentals")
// @Tag améliore la documentation Swagger.
@Tag(name = "Rentals")
public class RentalController {

    // Service métier injecté qui encapsule la logique sur les locations.
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // Endpoint GET paginé pour lister les annonces.
    @Operation(summary = "List rentals")
    @GetMapping
    public ResponseEntity<PageResponse<RentalResponse>> list(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(rentalService.list(pageable));
    }

    // Endpoint GET pour récupérer une annonce précise par son identifiant.
    @Operation(summary = "Get rental details")
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getById(id));
    }

    // Endpoint POST de création d'une annonce (multipart car on peut envoyer une image).
    @Operation(summary = "Create rental")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<RentalResponse> create(@Valid @ModelAttribute RentalRequest request,
                                                 Authentication authentication,
                                                 UriComponentsBuilder uriComponentsBuilder) {
        RentalResponse response = rentalService.create(request, authentication.getName());
        return ResponseEntity.created(uriComponentsBuilder.path("/api/rentals/{id}").buildAndExpand(response.id()).toUri())
                .body(response);
    }

    // Endpoint PUT pour modifier une annonce existante.
    @Operation(summary = "Update rental")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<RentalResponse> update(@PathVariable Long id,
                                                 @Valid @ModelAttribute RentalRequest request,
                                                 Authentication authentication) {
        RentalResponse response = rentalService.update(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
