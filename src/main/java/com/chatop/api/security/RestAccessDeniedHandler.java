package com.chatop.api.security;

// Le package security regroupe les composants liés à la protection de l'API (authentification, autorisations...).

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

// @Component permet à Spring de détecter automatiquement cette classe et de l'enregistrer dans le contexte.
// AccessDeniedHandler est invoqué lorsque l'utilisateur est authentifié mais n'a pas les droits nécessaires.
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    // ObjectMapper sérialise l'objet Java en JSON afin de renvoyer une réponse lisible par le client.
    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Méthode appelée automatiquement par Spring Security lors d'un accès refusé (HTTP 403).
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Corps JSON standardisé : cela aide le front-end à afficher un message adapté.
        Map<String, Object> body = Map.of(
                "status", HttpServletResponse.SC_FORBIDDEN,
                "error", "Forbidden",
                "message", accessDeniedException.getMessage(),
                "path", request.getRequestURI(),
                "timestamp", OffsetDateTime.now().toString()
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
