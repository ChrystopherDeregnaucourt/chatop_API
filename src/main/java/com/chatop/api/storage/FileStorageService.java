package com.chatop.api.storage;

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

@Slf4j
@Service
public class FileStorageService {

    private final Path rootLocation;
    private final FileStorageProperties properties;

    public FileStorageService(FileStorageProperties properties) {
        this.properties = properties;
        this.rootLocation = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create storage directory", e);
        }
    }

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), "file");
        String extension = FilenameUtils.getExtension(originalFilename);
        String sanitizedExtension = extension != null && !extension.isBlank() ? "." + extension.replaceAll("[^a-zA-Z0-9]", "") : "";
        String storedFilename = UUID.randomUUID() + sanitizedExtension;
        Path destinationFile = this.rootLocation.resolve(storedFilename).normalize();

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

    public String getContentType(Resource resource) {
        try {
            String detected = Files.probeContentType(Path.of(resource.getURI()));
            return detected != null ? detected : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        } catch (IOException e) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

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
