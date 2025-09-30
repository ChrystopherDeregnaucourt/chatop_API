package com.chatop.api.auth.service;

// Le package service contient les classes qui implémentent la logique métier.
// AuthService orchestre ici toutes les opérations liées à l'inscription et à la connexion.

import com.chatop.api.auth.dto.*;
import com.chatop.api.security.jwt.JwtTokenService;
import com.chatop.api.user.UserService;
import com.chatop.api.user.dto.UserResponse;
import com.chatop.api.user.mapper.UserMapper;
import com.chatop.api.user.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// @Service indique à Spring que cette classe représente un composant métier.
// Elle sera instanciée automatiquement et injectable dans d'autres classes (ex. le contrôleur).
@Service
public class AuthService {

    // AuthenticationManager est l'outil central de Spring Security pour vérifier les identifiants.
    private final AuthenticationManager authenticationManager;
    // Service maison chargé de créer et lire les tokens JWT.
    private final JwtTokenService jwtTokenService;
    // UserService manipule les entités User (création, recherche en base, ...).
    private final UserService userService;
    // UserMapper convertit une entité User vers un DTO UserResponse adapté à l'exposition API.
    private final UserMapper userMapper;

    // Le constructeur liste les dépendances nécessaires. Spring l'utilise pour injecter automatiquement
    // les implémentations correspondantes, ce qui favorise l'inversion de contrôle et facilite les tests unitaires.
    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       UserService userService,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Inscription d'un nouvel utilisateur suivie de la génération d'un token de connexion automatique.
    public RegisterResponse register(RegisterRequest request) {
        // On délègue la création de l'utilisateur au UserService qui applique toutes les règles (hash du mot de passe, etc.).
        User user = userService.registerUser(request.name(), request.email(), request.password());
        // On authentifie immédiatement l'utilisateur pour lui éviter une connexion manuelle juste après l'inscription.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));
        // Le SecurityContextHolder stocke l'utilisateur authentifié pour la durée de la requête.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Nous récupérons les détails (notamment le nom d'utilisateur) pour fabriquer un token JWT personnalisé.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Génération du token contenant les informations de l'utilisateur et la date d'expiration.
        String token = jwtTokenService.generateToken(userDetails);
        // On renvoie à la fois le profil API et les infos d'authentification dans un seul objet de réponse.
        return new RegisterResponse(userMapper.toResponse(user), AuthResponse.bearer(token, jwtTokenService.getExpirationSeconds()));
    }

    // Authentifie un utilisateur existant et renvoie un nouveau token JWT.
    public AuthResponse login(LoginRequest request) {
        // Le login est converti en minuscules pour éviter qu'une différence de casse n'empêche la connexion.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login().toLowerCase(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenService.generateToken(userDetails);
        return AuthResponse.bearer(token, jwtTokenService.getExpirationSeconds());
    }

    // Récupère les informations publiques d'un utilisateur à partir de son email.
    public UserResponse me(String email) {
        User user = userService.getByEmail(email);
        return userMapper.toResponse(user);
    }
}
