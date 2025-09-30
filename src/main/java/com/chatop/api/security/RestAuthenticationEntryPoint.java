package com.chatop.api.security;

// Gestionnaire personnalisé déclenché lorsqu'une requête non authentifiée tente d'accéder à une ressource protégée.

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

// @Component pour l'enregistrer auprès de Spring Security.
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // ObjectMapper pour produire une réponse JSON formatée.
    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Méthode appelée lorsqu'un client non authentifié accède à une route nécessitant une authentification.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = Map.of(
                "status", HttpServletResponse.SC_UNAUTHORIZED,
                "error", "Unauthorized",
                "message", authException.getMessage(),
                "path", request.getRequestURI(),
                "timestamp", OffsetDateTime.now().toString()
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
