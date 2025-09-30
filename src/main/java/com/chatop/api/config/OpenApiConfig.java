package com.chatop.api.config;

// Le package "config" regroupe les classes qui gèrent la configuration technique de l'application.
// Nous y plaçons ici tout ce qui concerne la documentation OpenAPI/Swagger.

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// L'annotation @Configuration indique à Spring que cette classe déclare des beans (composants gérés par Spring).
// Elle sera automatiquement scannée au démarrage pour enregistrer ces beans dans le contexte d'application.
@Configuration
public class OpenApiConfig {

    // L'annotation @Bean signale à Spring que la méthode retourne un composant à gérer.
    // Ici, nous construisons la description OpenAPI utilisée par Swagger pour documenter les endpoints de l'API.
    @Bean
    public OpenAPI chatopOpenAPI() {
        // Nous créons un objet OpenAPI en chaînant des appels de configuration lisibles.
        return new OpenAPI()
                // La section Info décrit globalement l'API (nom, description, version) et apparaît dans l'interface Swagger.
                .info(new Info()
                        .title("ChaTop Rental API")
                        .description("API REST de location immobilière")
                        .version("1.0.0"))
                // Nous ajoutons une exigence de sécurité pour rappeler qu'il faut transmettre un token JWT.
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                // La section components centralise les schémas réutilisables (ici, la configuration du schéma d'authentification).
                .components(new Components()
                        // Nous déclarons le schéma "bearerAuth" : Swagger affichera un bouton pour saisir un token JWT.
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                // Ce nom correspond à l'entête HTTP "Authorization" qui contiendra le jeton.
                                .name("Authorization")
                                // Nous spécifions un schéma HTTP classique.
                                .type(SecurityScheme.Type.HTTP)
                                // Le schéma "bearer" indique que le token est transmis via l'entête Authorization.
                                .scheme("bearer")
                                // bearerFormat permet de préciser le type de token ; ici il s'agit d'un JWT.
                                .bearerFormat("JWT")));
    }
}
