package com.chatop.api.user.repository;

// Les repositories s'appuient sur Spring Data JPA pour générer automatiquement les requêtes SQL courantes.

import com.chatop.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// En étendant JpaRepository, on bénéficie de nombreuses méthodes (findAll, save, delete...) sans code supplémentaire.
public interface UserRepository extends JpaRepository<User, Long> {
    // Recherche d'un utilisateur à partir de son email ; Optional évite les NullPointerException.
    Optional<User> findByEmail(String email);

    // Vérifie rapidement l'existence d'un email pour empêcher les doublons.
    boolean existsByEmail(String email);
}
