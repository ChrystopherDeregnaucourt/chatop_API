package com.chatop.api.rental.model;

// Entité JPA représentant une annonce de location.

import com.chatop.api.user.model.User;
import com.chatop.api.message.model.Message;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Lombok supprime le code cérémonial (getters, setters, constructeurs...).
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Entité persistée en base dans la table "rentals".
@Entity
@Table(name = "rentals")
public class Rental {

    // Identifiant unique auto-incrémenté.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de l'annonce.
    @Column(nullable = false)
    private String name;

    // Surface en m².
    @Column(nullable = false)
    private Integer surface;

    // Prix demandé.
    @Column(nullable = false)
    private Integer price;

    // Description libre ; type TEXT pour accepter un contenu long.
    @Column(columnDefinition = "TEXT")
    private String description;

    // Chemin du fichier image stocké.
    @Column(name = "picture_path", nullable = false, length = 500)
    private String picturePath;

    // Propriétaire de la location ; association obligatoire vers l'entité User.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Messages reçus pour cette annonce ; suppression en cascade si l'annonce est supprimée.
    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Message> messages = new HashSet<>();

    // Date de création enregistrée automatiquement.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Date de dernière mise à jour.
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Avant insertion : initialisation des timestamps.
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Avant mise à jour : rafraîchissement du champ updatedAt.
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
