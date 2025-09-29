# ChaTop Rental API

API REST de location immobilière construite avec Spring Boot 3, sécurisée par JWT et prête pour la production.

## Fonctionnalités

- Authentification par email/mot de passe avec jeton JWT (BCrypt pour le hashage).
- Gestion des utilisateurs, locations et messages.
- Upload et diffusion d'images via stockage local configurable.
- Pagination/tri des locations avec DTO dédiés.
- Gestion d'erreurs uniforme (format JSON), validation Bean Validation.
- Documentation OpenAPI/Swagger via Springdoc.
- Tests unitaires et d'intégration (Testcontainers MySQL).

## Prérequis

- Java 17+
- Maven 3.9+
- Docker (pour lancer MySQL via docker-compose)

## Variables d'environnement clés

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `SPRING_DATASOURCE_URL` | URL JDBC MySQL | `jdbc:mysql://localhost:3306/chatop?...` |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur DB | `chatop` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe DB | `chatop` |
| `JWT_SECRET` | Secret JWT **Base64 encodé** (32 octets min) | `Y2hhdG9wLWFwaS1kZWZhdWx0LXNlY3JldC1iYXNlNjQ=` |
| `JWT_EXPIRATION_SECONDS` | Durée de vie du token | `86400` |
| `FILE_STORAGE_PATH` | Dossier de stockage des images | `./storage` |
| `FILE_PUBLIC_URL` | URL publique des fichiers (optionnel) | vide |
| `SPRING_PROFILES_ACTIVE` | Profil Spring | `dev` |

> ⚠️ Le secret JWT doit impérativement être encodé en Base64. Pour générer un secret :
>
> ```bash
> openssl rand -base64 64
> ```

## Lancement rapide

1. Lancer la base MySQL :
   ```bash
   docker-compose up -d
   ```
2. Démarrer l'application :
   ```bash
   ./mvnw spring-boot:run
   ```
3. Accéder à la documentation Swagger : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Le profil `dev` applique Flyway pour créer le schéma (`db/migration`).

## Tests

Tests unitaires + intégration :
```bash
./mvnw clean test
```
Les tests d'intégration utilisent Testcontainers avec MySQL et créent un dossier temporaire pour les fichiers.

## Commandes utiles

- Build : `./mvnw clean package`
- Lancement (jar) : `java -jar target/chatop-api-0.0.1-SNAPSHOT.jar`
- Vérifier la santé : `GET /actuator/health`

## Exemples d'appels

Une collection `.http` est disponible : [`docs/requests.http`](docs/requests.http).

Exemple cURL pour créer une location :
```bash
curl -X POST http://localhost:8080/api/rentals \
  -H "Authorization: Bearer <TOKEN>" \
  -F "name=Loft Paris" \
  -F "surface=45" \
  -F "price=1200" \
  -F "description=Vue sur la Tour Eiffel" \
  -F "picture=@/chemin/vers/image.jpg"
```

## Structure du projet

```
src/main/java/com/chatop/api
├── auth
├── common
├── config
├── message
├── rental
├── security
├── storage
└── user
```

## Notes de sécurité

- Toutes les routes (`/api/**`) sont sécurisées par JWT, excepté l'inscription et la connexion.
- Gestion centralisée des erreurs (401/403/404/422) avec messages explicites.
- CORS permissif par défaut (à adapter selon l'environnement cible).
- Les mots de passe sont hashés avec BCrypt et jamais retournés dans les réponses.

## Déploiement

- Fournir `FILE_STORAGE_PATH` sur un volume persistant.
- Exporter les variables d'environnement sensibles (DB, JWT) via un coffre ou orchestrateur.
- Surveiller l'application via l'endpoint Actuator `/actuator/health`.

## Licence

Projet pédagogique ChaTop.
