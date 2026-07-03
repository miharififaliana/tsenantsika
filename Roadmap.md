\===============================================================================  
PHASE 0 – INITIALISATION DU PROJET ET STRUCTURE DE BASE  
\================================================================================

Objectif global :  
Mettre en place l'environnement de développement, la structure multi-modules,  
les dépendances et les configurations de base (Gradle, lint, tests).

Tâches précises :  
\- Créer le projet Android avec template "Empty Compose Activity".  
\- Configurer le build avec Gradle Kotlin DSL :  
  \* Appliquer les plugins : android-application, kotlin-android, kotlin-kapt, kotlin-parcelize.  
  \* Définir minSdk \= 21, targetSdk \= 30, compileSdk \= 30\.  
  \* Activer le support Java 8+ (desugaring).  
\- Créer la structure de packages :  
  com.boutique/  
    ├── data/ (entities, dao, repositories)  
    ├── network/ (clients Ktor, WebSocket)  
    ├── ui/ (écrans, composants)  
    ├── utils/ (extensions, helpers)  
    └── di/ (injection Koin)  
\- Ajouter les dépendances principales dans build.gradle.kts (module app) :  
  \* .

\- Configurer AndroidManifest.xml :  
  \* Permissions : INTERNET, ACCESS\_WIFI\_STATE, CHANGE\_WIFI\_MULTICAST\_STATE, ACCESS\_NETWORK\_STATE.  
  \* Ajouter \<uses-feature android:name="android.hardware.wifi" android:required="true"/\>.  
\- Mettre en place un système de logs (Timber).  
\- Créer un fichier build.gradle.kts pour le module :core et :app avec dépendances partagées (via version catalog).  
\- Configurer le serveur Ktor pour qu'il démarre au lancement de l'app (uniquement si rôle Patronne).  
\- Mettre en place un mécanisme de bascule : détection du rôle pour démarrer serveur ou client.

Fichiers impactés :  
\- app/build.gradle.kts  
\- app/src/main/AndroidManifest.xml  
\- app/src/main/java/com/boutique/BoutiqueApplication.kt (Application custom)  
\- app/src/main/java/com/boutique/di/AppModule.kt (module Koin)  
\- app/src/main/java/com/boutique/utils/Logger.kt  
\- gradle/libs.versions.toml (ou buildSrc)

Critères d'acceptation :  
\- Le projet compile sans erreur.  
\- L'application s'installe sur un émulateur API 21+.  
\- Les permissions réseau sont déclarées.  
\- Un log simple affiche "Serveur Ktor démarré" ou "Client connecté" (simulation).  
\- Les tests unitaires simples (exemple) passent.

\================================================================================  
PHASE 1 – MODÉLISATION DES DONNÉES (ROOM) ET DAO  
\================================================================================

Objectif global :  
Définir toutes les entités, leurs relations et implémenter les DAO pour les  
opérations CRUD de base, avec un dépôt central (Repository).

Tâches précises :  
\- Créer les classes de données (data class) pour chaque entité du dictionnaire,  
  annotées avec @Entity, @PrimaryKey, @ForeignKey, @Relation.  
\- Définir les enum : Role, StatutJournee, StatutVente, ActionAudit.  
\- Créer la classe abstraite BoutiqueDatabase étendant RoomDatabase,  
  listant les entités et les DAO.  
\- Écrire les DAO pour :  
  \* UtilisateurDao (CRUD, findByNom, findByPin, liste employés actifs)  
  \* JourneeDao (créer, lire journée ouverte, mettre à jour clôture)  
  \* CategoriePrixDao (CRUD, liste actives)  
  \* ParametreCommissionDao (récupérer le dernier paramètre)  
  \* VenteDao (CRUD, ventes par journée, par vendeur, avec jointures)  
  \* LigneVenteDao (CRUD par vente)  
  \* AvanceEmployeDao (CRUD, avances par employé et journée)  
  \* DepenseBoutiqueDao (CRUD, dépenses par journée)  
  \* AuditLogDao (insérer, récupérer par entité, par date)  
\- Implémenter des requêtes complexes pour les statistiques (total par catégorie,  
  chiffre d'affaires par journée, commissions brutes par employé).  
\- Ajouter des migrations de base (version 1).  
\- Créer un BaseRepository fournissant les DAO et gérant les transactions.  
\- Écrire des tests unitaires (Robolectric ou Room.inMemory) pour valider chaque DAO.

Fichiers impactés :  
\- core/src/main/java/com/boutique/data/entities/\*.kt (toutes les entités)  
\- core/src/main/java/com/boutique/data/dao/\*Dao.kt  
\- core/src/main/java/com/boutique/data/database/BoutiqueDatabase.kt  
\- core/src/main/java/com/boutique/data/repositories/\*Repository.kt  
\- core/src/test/java/com/boutique/data/dao/\*DaoTest.kt

Critères d'acceptation :  
\- La base de données Room se crée et se met à jour (version 1\) sans erreur.  
\- Tous les DAO passent les tests unitaires avec des données fictives.  
\- Les relations (one-to-many) sont correctement gérées (@Relation).  
\- Les contraintes (clés étrangères, cascade) sont définies et respectées.  
\- Les requêtes de statistiques renvoient des résultats cohérents.  
\- La base peut être inspectée via le Database Inspector d'Android Studio.

\================================================================================  
PHASE 2 – AUTHENTIFICATION ET GESTION DES UTILISATEURS  
\================================================================================

Objectif global :  
Mettre en place le système d'authentification (PIN), la gestion des rôles,  
la création du compte "Patronne" par défaut et la gestion des employés (CRUD par la Patronne).

Tâches précises :  
\- Créer un écran de connexion (LoginScreen) avec champ nom, champ PIN (4 chiffres),  
  bouton "Se connecter".  
\- Implémenter le LoginViewModel utilisant UtilisateurRepository pour valider les identifiants.  
\- Après authentification réussie, déterminer le rôle et naviguer vers l'écran principal approprié.  
\- Ajouter un écran "Paramètres" pour permettre à l'utilisateur connecté de modifier son propre PIN (avec validation de l'ancien).  
\- Ajouter une interface d'administration pour la Patronne :  
  \* Liste des employés (actifs/inactifs).  
  \* Formulaire d'ajout/modification d'un employé (nom, PIN, actif).  
  \* Désactivation (suppression logique) d'un employé.  
\- Intégrer la gestion de session (conservation de l'utilisateur connecté via DataStore ou SharedPreferences).  
\- Gérer le cas où aucun utilisateur n'existe : lancer automatiquement l'écran de création du compte Patronne (un seul compte Patronne autorisé).  
\- Ajouter des tests unitaires pour le repository d'authentification et le ViewModel.

Fichiers impactés :  
\- app/src/main/java/com/boutique/ui/auth/LoginScreen.kt  
\- app/src/main/java/com/boutique/ui/auth/LoginViewModel.kt  
\- app/src/main/java/com/boutique/ui/auth/EditPinScreen.kt  
\- app/src/main/java/com/boutique/ui/admin/EmployeManagementScreen.kt  
\- app/src/main/java/com/boutique/data/repositories/AuthRepository.kt  
\- app/src/main/java/com/boutique/utils/SessionManager.kt (DataStore)  
\- Ajout des routes de navigation dans NavGraph.

Critères d'acceptation :  
\- La connexion avec un PIN correct (Patronne ou Employé) redirige vers le bon espace.  
\- Un PIN erroné affiche un message d'erreur.  
\- La Patronne peut créer un compte employé avec nom/PIN, le modifier, le désactiver.  
\- Un employé désactivé ne peut pas se connecter.  
\- Un employé peut modifier son propre PIN via les paramètres (ancien PIN requis).  
\- La session persiste après redémarrage de l'app.  
\- Si aucun compte n'existe, l'écran de création de la Patronne apparaît.

\================================================================================  
PHASE 3 – MISE EN PLACE DU SERVEUR KTOR (CÔTÉ PATRONNE)  
\================================================================================

Objectif global :  
Intégrer Ktor Server dans l'application Patronne, configurer les endpoints REST  
de base et le WebSocket, et assurer la découverte du service (NSD).

Tâches précises :  
\- Créer une classe KtorServerManager qui initialise le serveur sur un port fixe (ex: 8080\) avec :  
  \* install(ContentNegotiation) avec kotlinx.serialization (JSON)  
  \* install(WebSockets) pour les canaux temps réel  
  \* install(CORS) pour autoriser les requêtes depuis le réseau local  
  \* Logging des requêtes  
\- Définir les routes REST pour :  
  \* GET /api/journee/actuelle → retourne la journée ouverte  
  \* POST /api/vente → créer une vente (avec lignes)  
  \* PUT /api/vente/{id} → modifier une vente  
  \* DELETE /api/vente/{id} → supprimer une vente (avec motif)  
  \* POST /api/avance → ajouter une avance  
  \* POST /api/depense → ajouter une dépense  
  \* GET /api/employes → liste des employés actifs  
  \* GET /api/stats/journalieres → statistiques de la journée  
  \* (autres endpoints nécessaires)  
\- Implémenter les handlers REST en utilisant les Repositories (injection Koin).  
\- Configurer le WebSocket à l'adresse ws://\<ip\>:8080/ws :  
  \* Ajouter les sessions connectées à une liste.  
  \* À chaque modification de données, diffuser un message JSON contenant le type d'événement.  
  \* Gérer la déconnexion proprement.  
\- Mettre en place la découverte de service :  
  \* Utiliser NsdManager pour enregistrer un service de type \_boutique.\_tcp avec le port du serveur.  
  \* Démarrer l'enregistrement dès que le serveur est lancé.  
\- Démarrer le serveur automatiquement lorsque l'application Patronne est en premier plan (ou en arrière-plan via un Service).  
\- Gérer l'arrêt du serveur lorsque la Patronne quitte l'app (ou le laisser tourner en arrière-plan).

Fichiers impactés :  
\- core/src/main/java/com/boutique/network/server/KtorServerManager.kt  
\- core/src/main/java/com/boutique/network/server/Routes.kt  
\- core/src/main/java/com/boutique/network/server/WebSocketHandler.kt  
\- core/src/main/java/com/boutique/network/server/ServiceDiscovery.kt  
\- core/src/main/java/com/boutique/network/models/Event.kt (DTO pour WebSocket)  
\- app/src/main/java/com/boutique/BoutiqueApplication.kt (démarrer le serveur si rôle Patronne)

Critères d'acceptation :  
\- Le serveur Ktor démarre sans erreur sur le téléphone de la Patronne.  
\- Les endpoints REST répondent correctement (testés avec Postman ou client REST depuis un autre appareil).  
\- Le service est détectable par NSD (outil de découverte).  
\- Le WebSocket accepte les connexions et diffuse des messages simples (ex: "ping").  
\- Les logs montrent les requêtes entrantes.

\================================================================================  
PHASE 4 – CLIENT RÉSEAU (EMPLOYÉ) ET SYNCHRONISATION INITIALE  
\================================================================================

Objectif global :  
Créer le module client : découverte du serveur, connexion WebSocket, récupération  
initiale des données via REST, et mise en cache locale (Room).

Tâches précises :  
\- Implémenter un NetworkClient qui :  
  \* Utilise NsdManager pour découvrir le service de la Patronne (ou saisie manuelle de l'IP en fallback).  
  \* Une fois l'IP trouvée, crée un client HTTP (Ktor client) avec les mêmes sérialisations.  
  \* Se connecte au WebSocket.  
\- Synchronisation initiale :  
  \* Appeler les endpoints REST pour récupérer :  
    \- La journée en cours (si ouverte)  
    \- La liste des employés actifs  
    \- Les catégories de prix  
    \- Le paramètre de commission actuel  
    \- Les ventes de la journée (pour l'employé connecté, et les agrégats globaux)  
    \- Les avances de l'employé  
    \- Les dépenses (pour la Patronne uniquement)  
  \* Insérer ces données dans la base locale Room de l'employé.  
\- Gérer les mises à jour via WebSocket :  
  \* À réception d'un événement, mettre à jour la base locale et notifier les ViewModels via StateFlow.  
\- Assurer la reconnexion automatique si la connexion WebSocket est perdue (tentative exponentielle).  
\- Afficher un indicateur de connectivité (connecté/déconnecté) dans l'UI.

Fichiers impactés :  
\- core/src/main/java/com/boutique/network/client/NetworkClient.kt  
\- core/src/main/java/com/boutique/network/client/ServiceDiscoveryClient.kt  
\- core/src/main/java/com/boutique/network/client/WebSocketClient.kt  
\- core/src/main/java/com/boutique/data/repositories/SyncRepository.kt (gère la synchronisation)  
\- app/src/main/java/com/boutique/ui/common/ConnectivityIndicator.kt

Critères d'acceptation :  
\- L'application employé découvre automatiquement le serveur de la Patronne (ou saisie manuelle).  
\- La connexion WebSocket s'établit et reste active.  
\- Les données initiales sont correctement copiées dans la base locale.  
\- Une modification (ex: création d'une vente) côté serveur déclenche un événement WebSocket et la base locale de l'employé est mise à jour en temps réel.  
\- La reconnexion automatique fonctionne (couper le Wi-Fi, rétablir, la connexion se rétablit).  
\- L'indicateur de connectivité change d'état.

\================================================================================  
PHASE 5 – GESTION DES JOURNÉES (OUVERTURE, CLÔTURE, RÉSUMÉ)  
\================================================================================

Objectif global :  
Implémenter le cycle de vie d'une journée de vente : ouverture, suivi des indicateurs  
en temps réel, clôture avec calculs finaux, et réinitialisation.

Tâches précises :  
\- Créer un écran "Tableau de bord Patronne" avec :  
  \* Bouton "Ouvrir une nouvelle journée" (si aucune journée ouverte).  
  \* Affichage du statut de la journée actuelle (ouverte/durée).  
  \* Indicateurs clés : CA brut, total dépenses, total avances, bénéfice net, nombre de ventes, etc. (mise à jour en temps réel).  
\- Implémenter la logique d'ouverture :  
  \* Vérifier qu'aucune autre journée n'est ouverte (contrainte R7).  
  \* Créer une nouvelle entrée Journee avec statut \= OUVERTE, heureOuverture \= now.  
  \* Réinitialiser les compteurs dans l'UI.  
\- Implémenter la logique de clôture :  
  \* Calculer les totaux à partir des données du jour.  
  \* Mettre à jour les champs chiffreAffaires, depensesBoutique, beneficeNet.  
  \* Passer le statut à CLOTUREE et enregistrer heureCloture.  
  \* Archiver (rien de spécial, la journée reste en base).  
  \* Proposer à la Patronne d'ouvrir immédiatement une nouvelle journée.  
\- Ajouter un écran "Historique des journées" pour consulter les journées passées.  
\- Dans l'UI Employé, afficher le résumé de la journée (ses propres ventes, commissions,  
  avances, et le nombre global de pièces vendues par les collègues).  
\- Ajouter un rafraîchissement manuel ou automatique des données.

Fichiers impactés :  
\- app/src/main/java/com/boutique/ui/patronne/JourneeScreen.kt  
\- app/src/main/java/com/boutique/ui/patronne/JourneeViewModel.kt  
\- app/src/main/java/com/boutique/ui/employe/ResumeJourneeScreen.kt  
\- app/src/main/java/com/boutique/data/repositories/JourneeRepository.kt (méthodes d'ouverture/clôture)  
\- app/src/main/java/com/boutique/data/repositories/StatsRepository.kt (calculs des totaux)

Critères d'acceptation :  
\- La Patronne peut ouvrir une journée (seulement si aucune autre ouverte).  
\- Les indicateurs se mettent à jour automatiquement après chaque vente/dépense/avance.  
\- La clôture calcule correctement les totaux et l'historique est enregistré.  
\- Après clôture, toutes les données sont en lecture seule.  
\- Un employé voit son résumé jour en cours et le nombre total de pièces vendues (tous vendeurs).  
\- L'historique des journées est accessible.

\================================================================================  
PHASE 6 – GESTION DES VENTES (PANIER, CRUD, COMMISSIONS)  
\================================================================================

Objectif global :  
Implémenter l'ensemble du processus de vente : création d'un panier multi-articles,  
validation, modification et suppression (avec audit).

Tâches précises :  
\- Écran de saisie de vente (Patronne uniquement) :  
  \* Sélection du vendeur (employé) via spinner ou autocomplete.  
  \* Liste des lignes de panier : chaque ligne avec catégorie (dropdown), quantité, sous-total calculé.  
  \* Bouton "Ajouter ligne" pour ajouter une nouvelle ligne.  
  \* Total des sous-totaux affiché.  
  \* Champ modifiable "Prix final négocié" (ajustement manuel) qui remplace le total.  
  \* Bouton "Valider la vente".  
\- Implémenter la logique métier dans VenteRepository :  
  \* Créer une vente :  
    \- Récupérer la commission unitaire en vigueur.  
    \- Calculer le nombre total de pièces \= somme des quantités.  
    \- Calculer la commission totale \= totalPièces \* commissionUnitaire.  
    \- Copier commissionUnitaireUtilisee et prixReferenceHistorique pour chaque ligne.  
    \- Enregistrer la vente et les lignes dans une transaction Room.  
    \- Mettre à jour les statistiques de la journée.  
    \- Diffuser un événement WebSocket VENTE\_CREEE.  
  \* Modifier une vente :  
    \- Vérifier que la journée est ouverte et que la vente n'est pas clôturée.  
    \- Recalculer les totaux et commissions.  
    \- Mettre à jour l'entité Vente et les Lignes (remplacement ou suppression/réinsertion).  
    \- Diffuser VENTE\_MODIFIEE.  
  \* Supprimer une vente :  
    \- Exiger un motif (texte non vide).  
    \- Supprimer la vente et ses lignes.  
    \- Enregistrer un log d'audit (AuditLogDao) avec le motif, l'id de la vente, l'utilisateur, date.  
    \- Recalculer les statistiques.  
    \- Diffuser VENTE\_SUPPRIMEE.  
\- Ajouter l'écran de liste des ventes de la journée (Patronne) avec possibilité de modifier/supprimer.  
\- Implémenter les appels REST correspondants (Phase 3\) pour que les employés puissent récupérer leurs ventes (lecture seule).  
\- Assurer que les modifications/suppressions sont interdites après clôture de la journée.

Fichiers impactés :  
\- app/src/main/java/com/boutique/ui/patronne/vente/VenteCreationScreen.kt  
\- app/src/main/java/com/boutique/ui/patronne/vente/VenteEditionScreen.kt  
\- app/src/main/java/com/boutique/ui/patronne/vente/VenteViewModel.kt  
\- core/src/main/java/com/boutique/data/repositories/VenteRepository.kt  
\- core/src/main/java/com/boutique/data/repositories/AuditRepository.kt  
\- core/src/main/java/com/boutique/network/server/Routes.kt (endpoints pour vente)

Critères d'acceptation :  
\- La Patronne peut créer une vente avec plusieurs lignes, le total final peut être ajusté.  
\- La commission est calculée en fonction du nombre total de pièces et de la commission unitaire en vigueur.  
\- Les valeurs historiques (commissionUnitaireUtilisee, prixReferenceHistorique) sont correctement enregistrées.  
\- La modification d'une vente recalcule tout et met à jour les statistiques.  
\- La suppression exige un motif et crée un log d'audit.  
\- Les ventes ne sont plus modifiables après clôture.  
\- Les employés voient leurs propres ventes (lecture seule) et les quantités globales.

\================================================================================  
PHASE 7 – GESTION DES AVANCES ET DÉPENSES  
\================================================================================

Objectif global :  
Permettre à la Patronne d'enregistrer les avances aux employés et les dépenses  
de la boutique, avec séparation stricte.

Tâches précises :  
\- Écran "Avances" :  
  \* Liste des avances de la journée (par employé).  
  \* Bouton "Ajouter une avance" → formulaire : sélection employé, montant, motif (optionnel).  
  \* Après validation, enregistrement dans AvanceEmploye (lié à la journée).  
  \* Mise à jour du solde de l'employé (affiché dans son résumé).  
\- Écran "Dépenses" :  
  \* Liste des dépenses de la journée.  
  \* Bouton "Ajouter une dépense" → formulaire : libellé, montant.  
  \* Enregistrement dans DepenseBoutique.  
\- Calcul du bénéfice net \= CA \- depensesBoutique (selon dictionnaire).  
  (Les commissions ne sont pas déduites du bénéfice net, mais le reste à payer des employés est calculé séparément.)  
\- Ajouter les endpoints REST et WebSocket pour ces opérations.  
\- Dans l'UI Employé, afficher le total des avances reçues et le "reste à payer" (commission brute \- avances).

Fichiers impactés :  
\- app/src/main/java/com/boutique/ui/patronne/AvancesScreen.kt  
\- app/src/main/java/com/boutique/ui/patronne/DepensesScreen.kt  
\- app/src/main/java/com/boutique/ui/patronne/AvancesViewModel.kt  
\- app/src/main/java/com/boutique/ui/employe/EmployeResumeScreen.kt (ajout du reste à payer)  
\- core/src/main/java/com/boutique/data/repositories/AvanceRepository.kt  
\- core/src/main/java/com/boutique/data/repositories/DepenseRepository.kt

Critères d'acceptation :  
\- La Patronne peut ajouter une avance, elle apparaît dans la liste et impacte le solde employé.  
\- La Patronne peut ajouter une dépense, elle apparaît dans la liste et impacte le bénéfice net.  
\- Les avances ne sont pas incluses dans les dépenses boutique.  
\- L'employé voit ses avances et son reste à payer (commissions brutes \- avances).  
\- Les données sont synchronisées en temps réel via WebSocket.

\================================================================================  
PHASE 8 – PARAMÈTRES SECRETS (CATÉGORIES, COMMISSION, MENU CACHÉ)  
\================================================================================

Objectif global :  
Implémenter les fonctionnalités d'administration réservées à la Patronne :  
modification des catégories de prix, ajout de nouvelles catégories, modification  
du montant de commission unitaire, via un menu caché.

Tâches précises :  
\- Créer un écran "Paramètres avancés" accessible uniquement par la Patronne  
  (par exemple, depuis l'écran des paramètres généraux, un bouton "Administration").  
\- Gestion des catégories :  
  \* Afficher la liste des catégories (actives et inactives).  
  \* Permettre de modifier le libellé et le prix de référence.  
  \* Permettre d'ajouter une nouvelle catégorie (libellé, prix).  
  \* Permettre de désactiver/réactiver une catégorie (toggle).  
  \* Lorsqu'une catégorie est modifiée, les ventes passées conservent leur prix historique (copie dans LigneVente).  
\- Gestion de la commission :  
  \* Afficher le montant actuel.  
  \* Permettre de modifier le montant (nouvelle valeur).  
  \* Enregistrer un nouveau ParametreCommission avec dateEffet \= maintenant, modifiePar \= Patronne.  
  \* La modification affecte uniquement les ventes futures.  
\- Ajouter les endpoints REST pour ces opérations (uniquement accessibles à la Patronne).  
\- Diffuser un événement WebSocket PARAMETRE\_COMMISSION\_MODIFIE et CATEGORIE\_MODIFIEE pour les employés.

Fichiers impactés :  
\- app/src/main/java/com/boutique/ui/patronne/AdminSettingsScreen.kt  
\- app/src/main/java/com/boutique/ui/patronne/AdminSettingsViewModel.kt  
\- core/src/main/java/com/boutique/data/repositories/CategoriePrixRepository.kt  
\- core/src/main/java/com/boutique/data/repositories/ParametreCommissionRepository.kt  
\- core/src/main/java/com/boutique/network/server/Routes.kt (nouveaux endpoints)

Critères d'acceptation :  
\- La Patronne peut modifier les catégories et ajouter des nouvelles ; les changements sont pris en compte immédiatement dans les formulaires de vente.  
\- La modification de la commission met à jour le paramètre en vigueur ; les nouvelles ventes utilisent le nouveau montant.  
\- Les employés reçoivent les mises à jour et adaptent leur affichage (ex: nouvelle catégorie disponible).  
\- Les historiques restent intacts.

\================================================================================  
PHASE 9 – AUDIT ET JOURNALISATION  
\================================================================================

Objectif global :  
Implémenter le journal d'audit pour toutes les actions sensibles (suppressions de ventes,  
modifications de commission, etc.) et fournir une interface de consultation pour la Patronne.

Tâches précises :  
\- Ajouter un interceptor dans les repositories pour enregistrer automatiquement les actions :  
  \* DELETE\_VENTE : avec motif, idVente, utilisateur.  
  \* MODIFICATION\_COMMISSION : ancienne valeur, nouvelle valeur.  
  \* MODIFICATION\_CATEGORIE : ancien prix, nouveau prix (si modifié).  
\- Créer un écran "Journaux d'audit" pour la Patronne :  
  \* Liste des logs avec date, action, utilisateur, détails.  
  \* Filtrage par type d'action ou par date.  
\- S'assurer que les logs sont persistants dans Room et ne sont jamais supprimés (sauf purge manuelle, non prévue).  
\- Ajouter des endpoints REST pour la consultation (lecture seule) et un événement WebSocket lors d'un nouvel enregistrement (optionnel).

Fichiers impactés :  
\- core/src/main/java/com/boutique/data/dao/AuditLogDao.kt (déjà créée)  
\- core/src/main/java/com/boutique/data/repositories/AuditRepository.kt (méthodes d'enregistrement)  
\- app/src/main/java/com/boutique/ui/patronne/AuditScreen.kt  
\- app/src/main/java/com/boutique/ui/patronne/AuditViewModel.kt

Critères d'acceptation :  
\- Toute suppression de vente génère un log avec le motif.  
\- Toute modification de commission génère un log.  
\- La Patronne peut consulter les logs dans l'application.  
\- Les logs ne sont pas modifiables.

\================================================================================  
PHASE 10 – FINALISATION UI/UX, TESTS ET OPTIMISATION  
\================================================================================

Objectif global :  
Peaufiner l'interface utilisateur selon la charte graphique, ajouter les retours  
utilisateur (toasts, dialogues), effectuer des tests intensifs, optimiser les  
performances et préparer la documentation.

Tâches précises :  
\- Appliquer la charte graphique :  
  \* Définir un thème Compose avec les couleurs : primaire \#2ECC71, secondaire \#3498DB,  
    alerte \#E74C3C, fonds \#FFFFFF et \#F8F9FA.  
  \* Utiliser les Material Themes personnalisées.  
  \* Ajouter des icônes Material Icons pour chaque action.  
\- Ajouter des dialogues de confirmation pour les actions critiques (suppression, clôture).  
\- Ajouter des messages d'erreur et de succès (Snackbar / Toast).  
\- Gérer les états de chargement (progress bars) lors des opérations réseau.  
\- Implémenter la gestion d'erreur réseau (affichage convivial, tentative de reconnexion).  
\- Optimiser les requêtes Room avec des indexes.  
\- Ajouter la prise en charge du mode hors-ligne : lors d'une déconnexion, les employés peuvent consulter les données en cache (lecture seule).  
\- Effectuer des tests :  
  \* Tests unitaires pour les repositories, viewmodels, DAO.  
  \* Tests d'intégration pour les endpoints Ktor (MockEngine).  
  \* Tests manuels sur plusieurs appareils (API 21 à 30\) avec différents scénarios.  
\- Rédiger une documentation utilisateur et une documentation technique.  
\- Préparer les fichiers de version (code signing, APK/AAB).

Fichiers impactés :  
\- app/src/main/java/com/boutique/ui/theme/Color.kt, Theme.kt  
\- Tous les fichiers UI pour ajouter les styles, icônes, dialogues.  
\- app/src/test/ et app/src/androidTest/ pour les tests.  
\- README.md, docs/ (documentation).

Critères d'acceptation :  
\- L'application respecte intégralement la charte graphique.  
\- Toutes les interactions critiques affichent une confirmation.  
\- Les erreurs sont gérées et affichées de manière compréhensible.  
\- Les tests unitaires couvrent au moins 70% du code métier.  
\- L'application fonctionne sur des appareils Android 5 à 11 (testé sur émulateurs et vrais téléphones).  
\- La performance est satisfaisante (pas de ralentissements, mémoire correcte).  
\- La documentation est livrée.

\================================================================================  
FIN DU PLAN D'ACTION  
\================================================================================