# Cash-Card

API REST Spring Boot pour gérer des « Cash Cards » (cartes de cash) avec persistance H2, sécurité Spring Security (authentification Basic), pagination/tri Spring Data et tests d’intégration.

Sommaire
- Présentation
- Prérequis
- Lancement de l’application
- Jeu de données (H2) et schéma
- Sécurité et utilisateurs de test
- Endpoints de l’API
- Exemples d’utilisation (cURL)
- Lancer les tests
- Structure du projet

Présentation
Cette application expose une API CRUD pour des Cash Cards, avec des règles d’accès basées sur le propriétaire de la carte. Les données sont stockées dans une base en mémoire (H2) initialisée au démarrage via schema.sql et data.sql. La sécurité s’appuie sur l’authentification HTTP Basic et des rôles simples.

Prérequis
- Java 17 (JDK 17)
- Gradle Wrapper (fourni dans le dépôt: gradlew / gradlew.bat)

Lancement de l’application
- Windows: exécuter dans une console à la racine du projet
  - gradlew.bat bootRun
- macOS/Linux:
  - ./gradlew bootRun

Par défaut, l’application démarre sur http://localhost:8080

Jeu de données (H2) et schéma
- Base: H2 en mémoire, initialisée à chaque démarrage
- Fichiers:
  - src/main/resources/schema.sql: définition de la table CASH_CARD
  - src/main/resources/data.sql: données de test (ID/AMOUNT/OWNER)

Sécurité et utilisateurs de test
- Authentification: HTTP Basic
- Rôles utilisés: CARD-OWNER (accès aux ressources /cashcards/**); NON-OWNER (accès restreint)
- Comptes déclarés (InMemoryUserDetailsManager):
  - sarah1 / abc123 – rôle: CARD-OWNER (possède les cartes 99, 100, 101)
  - kumar2 / xyz789 – rôle: CARD-OWNER (possède la carte 102)
  - hank-owns-no-cards / qrs456 – rôle: NON-OWNER

Règles d’accès (résumé)
- POST /cashcards est ouvert (utilisé par les tests). Les autres endpoints /cashcards/** nécessitent un utilisateur avec rôle CARD-OWNER, sauf GET sans en-tête Authorization, qui est autorisé après création (pour suivre l’URI Location).
- Un utilisateur ne peut consulter/modifier/supprimer que ses propres cartes.

Endpoints de l’API
Base: /cashcards

1) GET /cashcards/{id}
- Récupère une Cash Card par ID (si elle appartient à l’utilisateur authentifié). Sans Authorization, autorisé uniquement selon la configuration de sécurité en place (principalement pour les tests après création).
- Réponses:
  - 200 OK + body JSON
  - 404 NOT FOUND si inexistante ou non possédée

2) POST /cashcards
- Crée une Cash Card.
- Corps JSON minimal: { "id": null, "amount": 250.0 } – Le champ owner est géré côté serveur/tests.
- Comportement en cas de doublon d’ID fourni: 409 CONFLICT + en-tête Location pointant sur la ressource existante.
- Réponses:
  - 201 CREATED + en-tête Location: /cashcards/{id}

3) GET /cashcards
- Liste paginée/triée des cartes de l’utilisateur authentifié.
- Paramètres de requête standards Spring Data:
  - page (défaut 0), size (défaut 20), sort (ex: amount,asc | amount,desc)
- Réponse:
  - 200 OK + tableau JSON (contenu de la page)

4) PUT /cashcards/{id}
- Met à jour le montant d’une carte existante appartenant à l’utilisateur.
- Corps JSON: { "amount": 19.99 }
- Réponses:
  - 204 NO CONTENT si mise à jour OK
  - 401 UNAUTHORIZED si non authentifié
  - 404 NOT FOUND si la carte n’existe pas ou n’appartient pas à l’utilisateur

5) DELETE /cashcards/{id}
- Supprime une carte appartenant à l’utilisateur.
- Réponses:
  - 204 NO CONTENT si suppression OK
  - 401 UNAUTHORIZED si non authentifié
  - 404 NOT FOUND si la carte n’existe pas ou n’appartient pas à l’utilisateur

Exemples cURL
- Créer une carte (ouvert):
  curl -i -X POST http://localhost:8080/cashcards \
       -H "Content-Type: application/json" \
       -d '{"id": null, "amount": 250.00}'

- Récupérer une carte (auth Sarah):
  curl -i -u sarah1:abc123 http://localhost:8080/cashcards/99

- Lister cartes (auth Sarah) avec tri ascendant par amount (défaut):
  curl -i -u sarah1:abc123 "http://localhost:8080/cashcards?page=0&size=3"

- Lister cartes (auth Sarah) triées par amount desc:
  curl -i -u sarah1:abc123 "http://localhost:8080/cashcards?sort=amount,desc"

- Mettre à jour une carte (auth Sarah):
  curl -i -X PUT -u sarah1:abc123 \
       -H "Content-Type: application/json" \
       -d '{"amount": 19.99}' \
       http://localhost:8080/cashcards/99

- Supprimer une carte (auth Sarah):
  curl -i -X DELETE -u sarah1:abc123 http://localhost:8080/cashcards/99

Lancer les tests
- Windows: gradlew.bat test
- macOS/Linux: ./gradlew test

Structure du projet (principaux fichiers)
- src/main/java/org/example/cashcard
  - CashcardApplication.java – point d’entrée Spring Boot
  - CashCard.java – record entité (ID, AMOUNT, OWNER)
  - CashCardController.java – contrôleur REST
  - CashCardRepository.java – Spring Data JDBC (requêtes, pagination)
  - SecurityConfig.java – configuration Spring Security (HTTP Basic, rôles)
- src/main/resources
  - application.properties – configuration Spring Boot
  - schema.sql – schéma H2
  - data.sql – données de test
- src/test/java/org/example/cashcard
  - CashcardApplicationTests.java – tests d’intégration (REST)
  - CashCardJsonTest.java – tests JSON (sérialisation/désérialisation)

Licence
Ce projet est fourni à des fins d’apprentissage/démonstration. Ajoutez la licence de votre choix si nécessaire.
