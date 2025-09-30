package com.chatop.api.security.jwt;

// Filtre Spring Security dédié à l'authentification via un token JWT.

import com.chatop.api.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Slf4j crée automatiquement un logger (log.debug, log.info, ...).
@Slf4j
// @Component enregistre ce filtre dans le contexte Spring.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Service de gestion des tokens (extraction du username, validation...).
    private final JwtTokenService jwtTokenService;
    // Service personnalisé permettant de charger un utilisateur depuis la base à partir de son email.
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    // Méthode exécutée pour chaque requête HTTP : elle tente d'extraire un token et de l'utiliser pour authentifier l'utilisateur.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;
        final String username;

        // Si l'entête Authorization est manquante ou n'utilise pas le schéma Bearer, on laisse la requête continuer sans modification.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = header.substring(7);
        try {
            username = jwtTokenService.extractUsername(token);
        } catch (Exception ex) {
            // En cas de token invalide, on consigne l'information pour le débogage puis on laisse la chaîne poursuivre.
            log.debug("Failed to extract username from token", ex);
            filterChain.doFilter(request, response);
            return;
        }

        // On n'authentifie que si aucun utilisateur n'est déjà présent dans le contexte pour éviter d'écraser une authentification existante.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenService.isTokenValid(token, userDetails)) {
                // On crée un objet d'authentification sans mot de passe (déjà vérifié via le token).
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Poursuite de la chaîne de filtres : indispensable pour que la requête atteigne finalement le contrôleur.
        filterChain.doFilter(request, response);
    }
}
