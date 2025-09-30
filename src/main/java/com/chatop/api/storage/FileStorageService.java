package com.chatop.api.storage;

// Service responsable du stockage physique des fichiers (upload, suppression, lecture).

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

// @Slf4j fournit un logger pour tracer les opérations de stockage.
// @Service expose cette classe comme bean Spring injectible.
@Slf4j
@Service
public class FileStorageService {

    // Répertoire racine où les fichiers sont conservés.
    private final Path rootLocation;
    // Accès aux paramètres de configuration (chemin, URL publique).
    private final FileStorageProperties properties;

    public FileStorageService(FileStorageProperties properties) {
        this.properties = properties;
        // On normalise le chemin pour éviter les ambiguïtés et garantir la sécurité.
        this.rootLocation = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
        try {
            // On s'assure que le dossier existe au démarrage.
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create storage directory", e);
        }
    }

    // Enregistre un fichier reçu depuis une requête HTTP et renvoie son nom stocké.
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        // On récupère le nom d'origine pour conserver l'extension et logguer des informations utiles.
        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), "file");
        String extension = FilenameUtils.getExtension(originalFilename);
        // Nettoyage de l'extension pour empêcher l'injection de caractères spéciaux dans le nom du fichier.
        String sanitizedExtension = extension != null && !extension.isBlank() ? "." + extension.replaceAll("[^a-zA-Z0-9]", "") : "";
        // Utilisation d'un UUID pour éviter toute collision de noms de fichiers.
        String storedFilename = UUID.randomUUID() + sanitizedExtension;
        Path destinationFile = this.rootLocation.resolve(storedFilename).normalize();

        // Protection contre les attaques de traversal : on vérifie que le fichier reste dans le répertoire autorisé.
        if (!destinationFile.getParent().equals(this.rootLocation)) {
            throw new IllegalArgumentException("Cannot store file outside of the designated directory");
        }

        try {
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store file", ex);
        }

        log.info("Stored file {} ({} bytes) as {}", originalFilename, file.getSize(), storedFilename);
        return storedFilename;
    }

    // Supprime un fichier en ignorant silencieusement les noms vides.
    public void delete(String storedFilename) {
        if (!StringUtils.hasText(storedFilename)) {
            return;
        }
        Path filePath = this.rootLocation.resolve(storedFilename).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file {}", storedFilename, e);
        }
    }

    // Charge un fichier sous forme de Resource pour qu'il puisse être renvoyé dans une réponse HTTP.
    public Resource loadAsResource(String storedFilename) {
        try {
            Path file = rootLocation.resolve(storedFilename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new IllegalArgumentException("File not found: " + storedFilename);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not read file: " + storedFilename, e);
        }
    }

    // Détermine le type MIME du fichier pour définir l'entête Content-Type de la réponse HTTP.
    public String getContentType(Resource resource) {
        try {
            String detected = Files.probeContentType(Path.of(resource.getURI()));
            return detected != null ? detected : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        } catch (IOException e) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    // Construit l'URL publique permettant d'accéder au fichier.
    public String buildPublicUrl(String storedFilename) {
        if (!StringUtils.hasText(storedFilename)) {
            return null;
        }
        if (StringUtils.hasText(properties.getPublicUrl())) {
            return properties.getPublicUrl().replaceAll("/$", "") + "/" + storedFilename;
        }
        return "/files/" + storedFilename;
    }
}
