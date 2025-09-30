package com.chatop.api.message.repository;

// Interface Spring Data pour manipuler les messages en base.

import com.chatop.api.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

// Aucun code supplémentaire n'est nécessaire : JpaRepository fournit les opérations CRUD.
public interface MessageRepository extends JpaRepository<Message, Long> {
}
