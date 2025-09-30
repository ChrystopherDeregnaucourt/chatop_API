package com.chatop.api.message.model;

// Entité JPA représentant un message envoyé à propos d'une location.

import com.chatop.api.rental.model.Rental;
import com.chatop.api.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Lombok fournit les méthodes habituelles et les constructeurs.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Entité persistée dans la table "messages".
@Entity
@Table(name = "messages")
public class Message {

    // Identifiant auto-incrémenté du message.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contenu textuel ; type TEXT pour autoriser un message long.
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // Expéditeur du message (utilisateur connecté).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Location concernée par le message.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    // Timestamp de création.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Initialisation automatique du champ createdAt avant insertion.
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
