package com.chatop.api.rental.repository;

// Repository Spring Data pour manipuler les entités Rental.

import com.chatop.api.rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

// Hérite de toutes les opérations CRUD standard sans avoir à les implémenter manuellement.
public interface RentalRepository extends JpaRepository<Rental, Long> {
}
