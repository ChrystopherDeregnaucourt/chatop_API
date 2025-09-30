package com.chatop.api.auth.controller;

// La ligne ci-dessus indique le "package" dans lequel se trouve cette classe.
// Un package en Java est comme un dossier virtuel qui permet de ranger les classes
// par thématique. Ici, toutes les classes liées à l'authentification sont groupées
// dans `com.chatop.api.auth.controller`, ce qui facilite la recherche et évite les
// conflits de noms entre classes portant le même nom dans des contextes différents.

// Les `import` ci-dessous déclarent toutes les classes externes dont cette classe
// a besoin pour fonctionner. Sans eux, Java ne saurait pas où trouver les définitions
// des annotations, DTO ou services utilisés plus bas dans le fichier.
import com.chatop.api.auth.dto.*; // Import de tous les objets de transfert (DTO) liés à l'authentification.
import com.chatop.api.auth.service.AuthService; // Service métier qui gère la logique d'authentification.
import com.chatop.api.user.dto.UserResponse; // Objet renvoyé pour représenter un utilisateur côté API.
import io.swagger.v3.oas.annotations.Operation; // Annotation de documentation pour Swagger.
import io.swagger.v3.oas.annotations.tags.Tag; // Annotation pour regrouper les endpoints dans la doc.
import jakarta.validation.Valid; // Permet d'activer la validation automatique des requêtes entrantes.
import org.springframework.http.ResponseEntity; // Classe pratique pour personnaliser les réponses HTTP.
import org.springframework.security.core.Authentication; // Représentation de l'utilisateur connecté.
import org.springframework.web.bind.annotation.*; // Contient les annotations de mapping (`@PostMapping`, etc.).

// Les annotations ci-dessous indiquent à Spring que cette classe joue le rôle de
// "contrôleur" HTTP, c'est-à-dire qu'elle reçoit les requêtes du client et y répond.
@RestController
// Cette annotation précise le chemin commun (`/api/auth`) pour toutes les routes
// définies dans cette classe. Cela évite de répéter ce préfixe sur chaque méthode.
@RequestMapping("/api/auth")
// `@Tag` est une annotation utilisée par Swagger/OpenAPI pour générer une documentation
// claire. Elle regroupe les endpoints de ce contrôleur dans une section intitulée
// "Authentication" dans l'interface de documentation.
@Tag(name = "Authentication")
public class AuthController {

    // On déclare ici une dépendance vers `AuthService`. C'est le service qui contient
    // toute la logique métier liée à l'authentification (inscription, connexion, etc.).
    // Le mot-clé `final` signifie que cette variable sera initialisée une fois pour toutes
    // (dans le constructeur) et ne pourra plus pointer vers un autre objet ensuite.
    private final AuthService authService;

    // Ce constructeur sera automatiquement appelé par Spring. Grâce à l'injection
    // de dépendances, Spring fournit lui-même une instance d'`AuthService`. Nous n'avons
    // donc pas besoin de créer nous-même cet objet. Cela facilite les tests et respecte
    // le principe de responsabilité unique : le contrôleur orchestre, le service applique
    // la logique métier.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Cette méthode gère la route POST `/api/auth/register`. L'annotation `@Operation`
    // sert uniquement à documenter l'endpoint dans Swagger.
    @Operation(summary = "Register a new user")
    // `@PostMapping` précise qu'on attend une requête HTTP POST à l'URL `/register`.
    @PostMapping("/register")
    // L'annotation `@Valid` demande à Spring de vérifier que l'objet reçu respecte les
    // contraintes de validation définies dans `RegisterRequest` (par exemple champ
    // obligatoire, format d'email, etc.). Si la validation échoue, Spring renverra une
    // erreur automatiquement. `@RequestBody` signifie que les données JSON envoyées par le
    // client doivent être converties en un objet `RegisterRequest`.
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        // On délègue la création du compte au service métier. C'est lui qui se charge
        // d'appliquer toutes les règles (hash du mot de passe, vérification des doublons...).
        RegisterResponse response = authService.register(request);
        // On renvoie une réponse HTTP avec le code 201 (Created) pour indiquer que l'action
        // d'inscription a réussi. `ResponseEntity` permet de contrôler à la fois le corps de
        // la réponse et son code HTTP.
        return ResponseEntity.status(201).body(response);
    }

    // Cette méthode gère la connexion d'un utilisateur existant.
    @Operation(summary = "Authenticate a user")
    @PostMapping("/login")
    // Même fonctionnement que pour l'inscription : on valide l'objet `LoginRequest`
    // avant d'appeler le service. Une erreur de validation (ex. champ vide) arrête la
    // requête avant même d'atteindre la logique métier.
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // `authService.login` renvoie généralement un jeton (token JWT) ou des informations
        // d'authentification. On se contente de transmettre la réponse avec un code 200 (OK).
        return ResponseEntity.ok(authService.login(request));
    }

    // Cette méthode retourne les informations de l'utilisateur actuellement connecté.
    @Operation(summary = "Get current user")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        // Spring injecte automatiquement un objet `Authentication` représentant l'utilisateur
        // authentifié. `getName()` renvoie en général l'identifiant unique (souvent l'email).
        // On passe cette information au service qui récupère le profil complet.
        return ResponseEntity.ok(authService.me(authentication.getName()));
    }
}
