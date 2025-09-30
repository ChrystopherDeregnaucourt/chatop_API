package com.chatop.api.storage.controller;

// Contrôleur REST permettant d'exposer les fichiers stockés via HTTP.

import com.chatop.api.storage.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController associe la classe au mécanisme MVC de Spring et garantit des réponses JSON/ressources.
@RestController
// Les routes exposées commencent toutes par /files.
@RequestMapping("/files")
public class FileController {

    // Service dédié à la gestion des fichiers.
    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // Endpoint permettant de télécharger/afficher un fichier stocké.
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadAsResource(filename);
        String contentType = fileStorageService.getContentType(resource);
        return ResponseEntity.ok()
                // On renseigne le bon Content-Type pour que le client interprète correctement le fichier.
                .contentType(MediaType.parseMediaType(contentType))
                // Content-Disposition inline pour autoriser l'affichage direct dans le navigateur.
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .body(resource);
    }
}
