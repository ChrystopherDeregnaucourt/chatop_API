package com.chatop.api.common.exception;

// Cette exception personnalisée représente une erreur de validation ou de requête invalide (HTTP 400).
// Elle hérite de RuntimeException pour pouvoir être lancée sans être déclarée dans la signature des méthodes.

public class BadRequestException extends RuntimeException {
    // Le constructeur prend un message explicite qui sera renvoyé au client afin de faciliter la compréhension de l'erreur.
    public BadRequestException(String message) {
        super(message);
    }
}
