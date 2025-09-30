package com.chatop.api.security;

// Configuration centrale de Spring Security pour l'application.

import com.chatop.api.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// @Configuration indique que cette classe déclare des beans Spring.
// @EnableMethodSecurity active les annotations de sécurité sur les méthodes (ex: @PreAuthorize).
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Filtre personnalisé qui vérifie la présence d'un token JWT dans chaque requête.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // Gestionnaire déclenché lorsqu'un utilisateur non authentifié accède à une ressource protégée.
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    // Gestionnaire pour les cas où l'utilisateur est authentifié mais pas autorisé.
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RestAuthenticationEntryPoint authenticationEntryPoint,
                          RestAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // Déclare la chaîne de filtres de sécurité (équivalent moderne de WebSecurityConfigurerAdapter).
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactive CSRF car l'API est stateless et s'appuie sur les tokens.
                .csrf(csrf -> csrf.disable())
                // Active CORS avec une configuration personnalisée déclarée plus bas.
                .cors(Customizer.withDefaults())
                // Sessions stateless : l'état d'authentification est porté par le token, pas par la session serveur.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Personnalisation des réponses en cas d'erreur de sécurité.
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                // Définition des règles d'autorisation : certaines routes restent publiques, le reste nécessite une authentification.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/files/**", "/actuator/health").permitAll()
                        .anyRequest().authenticated())
                // Ajout du filtre JWT avant le filtre standard UsernamePasswordAuthenticationFilter.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Configuration CORS : ici très permissive car l'API peut être consommée depuis différents domaines.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(List.of("Location"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Bean responsable du hachage des mots de passe (BCrypt est une valeur sûre car adaptable et salée).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expose l'AuthenticationManager configuré par Spring pour pouvoir l'injecter dans AuthService.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
