package com.chatop.api;

// Nous déclarons le package pour organiser logiquement les classes de l'application.
// Ici, toutes les classes liées à l'API Chatop se trouvent dans "com.chatop.api".

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Cette annotation regroupe plusieurs fonctionnalités Spring Boot :
// - la détection automatique des composants
// - la configuration automatique
// - la possibilité de démarrer une application Spring Boot.
// Nous l'appliquons à la classe principale pour qu'elle serve de point d'entrée.
@SpringBootApplication
public class ChatopApiApplication {

    // La méthode main est le point d'entrée standard d'une application Java.
    // SpringApplication.run() démarre le contexte Spring, configure les beans et lance le serveur web.
    // Le tableau "args" permet de passer des arguments en ligne de commande si nécessaire.
    public static void main(String[] args) {
        SpringApplication.run(ChatopApiApplication.class, args);
    }
}
