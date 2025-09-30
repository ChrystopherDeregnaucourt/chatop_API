package com.chatop.api.user;

// Le package user contient la logique métier et les entités relatives aux utilisateurs de l'application.

import com.chatop.api.common.exception.BadRequestException;
import com.chatop.api.common.exception.ResourceNotFoundException;
import com.chatop.api.user.model.User;
import com.chatop.api.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// @Service indique que cette classe porte la logique métier liée aux utilisateurs et qu'elle est gérée par Spring.
@Service
public class UserService {

    // Repository Spring Data pour interagir avec la base de données (CRUD sur l'entité User).
    private final UserRepository userRepository;
    // PasswordEncoder assure le hachage sécurisé des mots de passe avant persistance.
    private final PasswordEncoder passwordEncoder;

    // Les dépendances sont injectées via le constructeur pour garantir l'immuabilité et faciliter les tests.
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // @Transactional garantit que toutes les opérations de cette méthode sont atomiques :
    // si une étape échoue, tout est annulé pour éviter des données incohérentes.
    @Transactional
    public User registerUser(String name, String email, String rawPassword) {
        // Normaliser l'email permet d'éviter les doublons dus à la casse (ex: "Test@" vs "test@...").
        String normalizedEmail = email.toLowerCase();
        // On vérifie que l'email n'est pas déjà utilisé afin de respecter la contrainte métier d'unicité.
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered");
        }
        // Construction de l'entité User via le builder généré par Lombok (présent dans la classe User).
        User user = User.builder()
                .name(name)
                .email(normalizedEmail)
                // On stocke uniquement le hash du mot de passe, jamais le mot de passe brut, pour des raisons de sécurité.
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
        // Persistons l'entité en base : save() retourne l'entité mise à jour (avec son identifiant, dates, ...).
        return userRepository.save(user);
    }

    // Récupère un utilisateur par email ou renvoie une exception claire s'il n'existe pas.
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Récupère un utilisateur par identifiant ; la logique d'erreur est identique pour uniformiser les réponses.
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
