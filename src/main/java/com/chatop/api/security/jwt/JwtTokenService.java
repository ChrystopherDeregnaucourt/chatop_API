package com.chatop.api.security.jwt;

// Ce package regroupe les classes utilitaires pour gérer les JSON Web Tokens (JWT).

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

// @Component signale que ce service est un bean Spring réutilisable dans toute l'application.
@Component
public class JwtTokenService {

    // Les propriétés (clé secrète, durée de validité...) sont injectées via JwtProperties.
    private final JwtProperties properties;
    // Clé symétrique utilisée pour signer et vérifier les tokens.
    private final SecretKey secretKey;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        // La clé est fournie en Base64 dans la configuration : on la décode puis on construit une clé HMAC SHA.
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));
    }

    // Génère un token signé contenant l'identité de l'utilisateur et la date d'expiration.
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(properties.getExpirationSeconds());
        return Jwts.builder()
                // Subject : identifiant principal du token (ici le login de l'utilisateur).
                .setSubject(userDetails.getUsername())
                // On ajoute des claims supplémentaires si besoin (ici un alias "uid").
                .addClaims(Map.of("uid", userDetails.getUsername()))
                // Date d'émission : utile pour invalider un token trop ancien.
                .setIssuedAt(Date.from(now))
                // Date d'expiration : protège contre l'utilisation d'un token indéfiniment.
                .setExpiration(Date.from(expiry))
                // Signature avec la clé secrète et l'algorithme HS256.
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Vérifie que le token correspond bien à l'utilisateur attendu et qu'il n'est pas expiré.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Récupère le login stocké dans le sujet du token.
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // Détermine si la date d'expiration est passée.
    public boolean isTokenExpired(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Méthode interne qui parse le token et renvoie les "claims" (données embarquées) après validation de la signature.
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Expose la durée de validité (en secondes) pour que d'autres composants puissent l'afficher ou la transmettre.
    public long getExpirationSeconds() {
        return properties.getExpirationSeconds();
    }
}
