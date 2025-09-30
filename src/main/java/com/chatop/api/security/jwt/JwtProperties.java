package com.chatop.api.security.jwt;

// Cette classe charge les propriétés de configuration liées aux JWT depuis le fichier application.yml/properties.

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// @Configuration indique que cette classe peut être utilisée comme source de configuration Spring.
@Configuration
// @ConfigurationProperties lit les propriétés commençant par "app.jwt" et les mappe sur les champs de cette classe.
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Secret used to sign JWT tokens.
     */
    private String secret;

    /**
     * JWT validity in seconds.
     */
    private long expirationSeconds;

    // Getters et setters standards afin que Spring puisse injecter les valeurs configurées.
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }
}
