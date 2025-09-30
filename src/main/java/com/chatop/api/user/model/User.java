package com.chatop.api.user.model;

// Ce package contient les entités JPA représentant les tables de la base de données relatives aux utilisateurs.

import com.chatop.api.rental.model.Rental;
import com.chatop.api.message.model.Message;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Les annotations Lombok (@Getter, @Setter, etc.) génèrent automatiquement les méthodes usuelles
// afin d'éviter le code répétitif. Cela rend la classe plus concise et lisible.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// @Entity indique qu'il s'agit d'une entité JPA persistée en base de données.
@Entity
// @Table permet de personnaliser le nom de la table (ici "users").
@Table(name = "users")
public class User {

    // Clé primaire auto-incrémentée de l'utilisateur.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de l'utilisateur, champ obligatoire.
    @Column(nullable = false)
    private String name;

    // Email unique ; on limite la longueur à 320 caractères conformément aux recommandations pour les emails.
    @Column(nullable = false, unique = true, length = 320)
    private String email;

    // Hash du mot de passe stocké ; on ne sauvegarde jamais le mot de passe brut.
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Date de création du compte, remplie automatiquement avant insertion.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Date de dernière mise à jour, également gérée automatiquement.
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relation 1-n avec les annonces de location. orphanRemoval = true supprime les locations orphelines lorsque l'utilisateur disparait.
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Rental> rentals = new HashSet<>();

    // Relation 1-n avec les messages envoyés par l'utilisateur.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Message> messages = new HashSet<>();

    // Méthode de cycle de vie JPA : appelée avant l'insertion pour initialiser createdAt et updatedAt.
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Méthode de cycle de vie JPA : appelée avant chaque mise à jour pour rafraîchir updatedAt.
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
