package com.chatop.api.message.controller;

// Contrôleur gérant l'envoi de messages par les locataires potentiels.

import com.chatop.api.message.dto.MessageRequest;
import com.chatop.api.message.dto.MessageResponse;
import com.chatop.api.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController transforme automatiquement les réponses en JSON.
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages")
public class MessageController {

    // Service métier qui orchestre la création des messages.
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Endpoint POST pour envoyer un message au propriétaire d'une annonce.
    @Operation(summary = "Send a message to a rental owner")
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request,
                                                       Authentication authentication) {
        MessageResponse response = messageService.create(request, authentication.getName());
        return ResponseEntity.status(201).body(response);
    }
}
