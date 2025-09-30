package com.chatop.api.common.exception;

// Exception spécialisée pour signaler qu'une ressource (utilisateur, location, etc.) n'a pas été trouvée.
// Elle sera traduite en réponse HTTP 404 par le gestionnaire d'exceptions global.

public class ResourceNotFoundException extends RuntimeException {
    // Le message précisant la ressource manquante est fourni au constructeur.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
