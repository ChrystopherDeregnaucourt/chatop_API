package com.chatop.api.storage;

// Cette classe charge la configuration du stockage de fichiers (emplacement local, URL publique...).

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// @ConfigurationProperties lit les valeurs app.file-storage.* définies dans les fichiers de configuration.
@Configuration
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageProperties {

    // Emplacement physique (sur le disque) où les fichiers sont enregistrés.
    private String location;

    // URL publique permettant d'accéder aux fichiers via HTTP.
    private String publicUrl;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }
}
