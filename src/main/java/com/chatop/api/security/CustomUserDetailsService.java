package com.chatop.api.security;

// Service Spring Security chargé de fournir les informations nécessaires à l'authentification.

import com.chatop.api.user.model.User;
import com.chatop.api.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service permet l'injection automatique dans d'autres composants (comme le filtre JWT).
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repository injecté pour récupérer l'utilisateur depuis la base de données.
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Méthode contractuelle de UserDetailsService : elle convertit un email en objet UserDetails utilisé par Spring Security.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // On construit un objet UserDetails standard en fournissant email, hash du mot de passe et rôles.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
