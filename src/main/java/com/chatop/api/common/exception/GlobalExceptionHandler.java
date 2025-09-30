package com.chatop.api.common.exception;

// Ce package centralise les classes liées à la gestion des erreurs pour harmoniser les réponses de l'API.

import com.chatop.api.common.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice permet de centraliser la gestion des exceptions pour tous les contrôleurs REST.
// En étendant ResponseEntityExceptionHandler, on profite également des méthodes déjà prêtes de Spring.
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Cette méthode surcharge le traitement des erreurs de validation sur les @RequestBody annotés @Valid.
    // Elle transforme les violations de contraintes en un format JSON uniforme compréhensible côté client.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, org.springframework.http.HttpStatusCode status, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        // On parcourt toutes les erreurs (possibles plusieurs champs invalides) pour construire un dictionnaire : champ -> message.
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // On récupère la requête HTTP originale pour inclure l'URL dans la réponse, pratique pour retracer l'appel en cause.
        HttpServletRequest servletRequest = (HttpServletRequest) request.resolveReference(WebRequest.REFERENCE_REQUEST);
        ApiErrorResponse body = ApiErrorResponse.of(status.value(), "Validation Failed", "Input validation failed", servletRequest != null ? servletRequest.getRequestURI() : null, errors);
        // On renvoie la même entête que celle proposée par Spring tout en substituant notre corps personnalisé.
        return new ResponseEntity<>(body, headers, HttpStatus.valueOf(status.value()));
    }

    // Gestion dédiée lorsque l'application ne trouve pas la ressource demandée (404).
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI(), Map.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Gestion des erreurs de type 400 explicites (souvent déclenchées par nos propres contrôles métier).
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getRequestURI(), Map.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // IllegalArgumentException est parfois lancée par les bibliothèques standard ; on la mappe aussi sur un 400 lisible.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getRequestURI(), Map.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Filet de sécurité final : toute exception non prévue renvoie une erreur 500 avec un message générique.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(), request.getRequestURI(), Map.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
