# MiniLien — Java / Spring Boot

MiniLien est un petit service de raccourcissement d'URL : on lui donne une adresse longue, il renvoie un lien court
(`http://localhost:8080/r/aZ3k9Q`) qui redirige vers l'adresse d'origine et compte les visites.

Le périmètre est volontairement réduit. L'application fonctionne en local sur votre poste, **et c'est tout** :
pas de conteneur, pas d'automatisation, pas de monitoring. C'est à vous de les ajouter.

## Prérequis

- Java 21 (JDK)
- Maven 3.9 ou plus récent

## Lancer l'application

```bash
mvn clean install
```

```bash
java -jar target/minilien-1.0.0.jar
```

Vous pouvez aussi lancer `mvn spring-boot:run`.

L'application répond sur http://localhost:8080.

## Lancer les tests

```bash
mvn test
```

## L'API

| Méthode | Route | Rôle | Réponses |
|---|---|---|---|
| `POST` | `/api/liens` | Crée un lien court. Corps : `{"url": "https://..."}` | `201` + lien ; `400` |
| `GET` | `/api/liens` | Liste les liens, du plus récent au plus ancien | `200` |
| `GET` | `/api/liens/{code}` | Détail d'un lien, avec son nombre de visites | `200` ; `404` |
| `DELETE` | `/api/liens/{code}` | Supprime un lien. En-tête `X-Admin-Token` obligatoire | `204` ; `401` ; `404` |
| `GET` | `/r/{code}` | Redirige vers l'URL d'origine et compte la visite | `302` ; `404` |

Les erreurs ont toujours le même format : `{"code": "URL_INVALIDE", "message": "..."}`.
Codes possibles : `REQUETE_INVALIDE`, `URL_INVALIDE`, `LIEN_INTROUVABLE`, `ACCES_REFUSE`.

Les liens sont gardés **en mémoire** : ils disparaissent quand l'application redémarre. Il n'y a pas de base de données.

### Essayer avec curl

```bash
curl -i -X POST http://localhost:8080/api/liens -H "Content-Type: application/json" -d '{"url": "https://openclassrooms.com"}'
```

```bash
curl -i http://localhost:8080/r/<code>
```

```bash
curl http://localhost:8080/api/liens
```

## Configuration

La configuration se trouve dans `src/main/resources/application.properties` : port, adresse publique des liens courts (`minilien.base-url`) et jeton d'administration (`minilien.admin-token`).

## Organisation du code

```
src/main/java/com/openclassrooms/minilien/
├── MiniLienApplication.java      point d'entrée Spring Boot
└── lien/
    ├── LienController.java       routes /api/liens
    ├── RedirectionController.java  route /r/{code}
    ├── LienService.java          création, recherche, visites (stockage en mémoire)
    └── GestionErreurs.java       format des erreurs JSON
src/test/java/.../LienApiTest.java  tests de l'API (MockMvc)
```
