# Tsenantsika

Application Android de gestion de boutique en réseau local (Wi-Fi).

## Rôles

- **Patronne** : ventes, journées, avances, dépenses, employés, paramètres avancés
- **Employé** : consultation de ses ventes, commissions, avances (lecture seule)

## Stack

Kotlin · Jetpack Compose · Room · Ktor (serveur embarqué) · Koin · WebSocket · NSD

## Installation

1. Ouvrir le projet dans Android Studio
2. Sync Gradle
3. Installer sur appareil/émulateur API 21+

## Premier lancement

1. Créer le compte **Patronne** (nom + PIN 4 chiffres)
2. Ouvrir une journée depuis le tableau de bord
3. Les employés se connectent au hotspot Patronne et utilisent leur nom/PIN

## Réseau local

- Le téléphone Patronne fait office de serveur (port 8080)
- Service NSD : `_boutique._tcp.`
- Les employés découvrent automatiquement le serveur

## Charte graphique

| Élément | Couleur |
|---------|---------|
| Primaire | `#2ECC71` |
| Secondaire | `#3498DB` |
| Alerte | `#E74C3C` |
| Fond | `#FFFFFF` / `#F8F9FA` |

## Tests

```bash
./gradlew test
```

## Structure

```
app/
  data/       Room entities, DAO, repositories
  network/    Ktor server, client, WebSocket, NSD
  ui/         Écrans Compose par rôle
  di/         Modules Koin
```
