package com.chatop.api.rental.dto;

// Objet de transfert utilisé lors de la création ou la mise à jour d'une annonce de location.

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

// Lombok génère les getters et setters pour simplifier la classe.
@Getter
@Setter
public class RentalRequest {

    // Nom de la location affiché dans les listings ; obligatoire et limité en longueur pour éviter les abus.
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    // Surface du logement, en m², doit être renseignée et positive.
    @NotNull(message = "Surface is required")
    @Positive(message = "Surface must be positive")
    private Integer surface;

    // Prix de la location ; également obligatoire et positif.
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Integer price;

    // Description libre de l'annonce ; non obligatoire mais documentée pour OpenAPI.
    @Schema(type = "string", description = "Rental description")
    private String description;

    // Image de la location envoyée en multipart/form-data. Swagger précise qu'il s'agit d'un fichier binaire.
    @Schema(type = "string", format = "binary", description = "Rental picture")
    private MultipartFile picture;
}
