\===============================================================================================  
CAHIER DES CHARGES COMPLET & SPÉCIFICATIONS FONCTIONNELLES (VERSION FINALE AVEC AJUSTEMENTS)  
PROJET : APPLICATION MOBILE DE GESTION DE BOUTIQUE EN RÉSEAU LOCAL (ANDROID)  
\===============================================================================================

1\. OBJECTIF GÉNÉRAL  
\-------------------------------------------------------------------------------  
Développer une application mobile Android dédiée à la gestion quotidienne d'une   
petite boutique de vente. L'application vise à centraliser le suivi des ventes,   
automatiser le calcul des commissions/salaires des employés, suivre les flux   
financiers (entrées/sorties) et offrir une visibilité en temps réel à la direction   
et aux équipes, le tout fonctionnant de manière autonome sans connexion Internet.

2\. ARCHITECTURE TECHNIQUE & CONNECTIVITÉ LOCALISÉE  
\-------------------------------------------------------------------------------  
\- Application 100% Hors-ligne : Pas de dépendance à un serveur Cloud externe.  
\- Mode Réseau Local SIMULÉ (Type "Jeu Multijoueur Local") :   
  Le smartphone de la Patronne fait office de serveur principal et de point d'accès   
  Wi-Fi (Hotspot). Les employés se connectent directement au point d'accès de la   
  Patronne avec leur propre téléphone pour synchroniser et consulter les données   
  en temps réel pendant la journée de travail.

\- SPÉCIFICATIONS DE LA GESTION DU RÉSEAU LOCAL ET DE LA RÉSILIENCE :  
  \* Persistance du serveur : L'application serveur (sur le téléphone de la gérante)   
    doit continuer à fonctionner en arrière-plan, même si elle utilise d'autres   
    applications simultanément.  
  \* Notifications de déconnexion : Si le hotspot est coupé ou si le téléphone de   
    la gérante redémarre, les employés doivent recevoir immédiatement un message   
    d'alerte ou de notification indiquant la perte de connexion.  
  \* Reconnexion automatique : Un employé doit pouvoir se reconnecter au serveur   
    instantanément et sans friction à tout moment, à condition que sa session soit   
    toujours active et ouverte.

3\. UTILISATEURS FINAUX, RÔLES & DROITS D'ACCÈS  
\-------------------------------------------------------------------------------  
L'application dispose de deux profils d'utilisateurs distincts :

A. Profil "Patronne" (Administrateur Général) :  
   \- Contrôle total et absolu sur l'application.  
   \- Droits complets de CRUD (Créer, Lire, Mettre à jour, Supprimer) sur toutes   
     les données : ventes, employés, catégories, avances et dépenses.  
   \- Saisie exclusive des ventes et attribution des commissions aux employés.  
   \- Gestion et saisie manuelle des dépenses de la boutique et des avances par employé.  
   \- Droit de modification d'une vente en cours de journée en cas d'erreur de saisie   
     (ex: erreur de vendeur ou sur le prix négocié) avant la clôture. La modification   
     déclenche un recalcul automatique des commissions de l'employé concerné, des   
     statistiques globales et du chiffre d'affaires.  
   \- Droit de suppression d'une vente avec obligation de fournir un motif (champ texte   
     requis). La suppression n'est pas tracée dans l'historique des ventes mais un   
     journal d'audit (log) est conservé avec le motif, l'identifiant de la vente   
     supprimée, l'utilisateur et la date/heure.  
   \- Accès complet aux archives historiques de toutes les journées passées.  
   \- Validation finale, clôture de la journée et possibilité d'ouvrir immédiatement   
     une nouvelle journée de vente.

B. Profil "Employés" (Vendeurs) \- Consultation Sécurisée :  
   \- Accès restreint via leur propre smartphone.  
   \- Connexion simplifiée et sécurisée à l'aide de leur Nom et d'un Code PIN unique   
     à 4 chiffres.  
   \- Possibilité de modifier son propre code PIN depuis les paramètres de l'application.  
   \- Aucune procédure de récupération de code PIN oublié en V1. En cas de perte,   
     une réinitialisation manuelle par la Patronne est nécessaire.  
   \- Suivi en temps réel des modifications apportées par la patronne.  
   \- Visibilité personnalisée et restreinte sur l'écran d'accueil :  
     \* Visualisation détaillée de ses propres ventes de la journée.  
     \* Nombre total de ventes (quantité de pièces) réalisées par ses collègues (sans voir leur chiffre d'affaires).  
     \* Nombre total global de ventes de la boutique pour la journée.  
     \* Salaire et commissions accumulés en temps réel basés sur ses propres ventes.  
     \* Suivi de ses propres avances perçues (saisies par la Patronne).  
     \* Un petit résumé historique de sa journée d'hier.

4\. PÉRIMÈTRE FONCTIONNEL & RÈGLES DE GESTION  
\-------------------------------------------------------------------------------

A. Gestion Dynamique des Catégories de Prix :  
   \- L'application est configurée avec 4 catégories de prix de base (valeurs initiales) :  
     \* Catégorie 1 : 3 000 Ar  
     \* Catégorie 2 : 15 000 Ar  
     \* Catégorie 3 : 25 000 Ar  
     \* Catégorie 4 : 35 000 Ar  
   \- Interface d'Administration Cachée : Pour contrer l'inflation ou faire évoluer   
     l'activité, la Patronne dispose d'un menu secret permettant de modifier les montants   
     de ces catégories (ex: passer une catégorie de 35 000 Ar à 40 000 Ar) ou d'ajouter   
     de nouvelles catégories de prix de référence.

B. Processus de Saisie d'un Panier Multi-Articles (Par la Patronne) :  
   Lors de l'enregistrement d'un achat client, le système gère un mode "Panier"   
   permettant d'ajouter plusieurs lignes avant la validation finale :  
   1\. Sélection du nom de l'employé responsable de la vente (bénéficiaire de la commission).  
   2\. Ajout d'articles au panier :  
      \- Sélection de la catégorie de prix concernée.  
      \- Saisie du nombre de pièces correspondantes.  
      \- Le sous-total pour cette ligne est automatiquement calculé (prix de référence × quantité).  
   3\. Possibilité d'ajouter d'autres lignes d'articles (ex: 1 pièce à 3 000 Ar \+ 2 pièces à 25 000 Ar).  
   4\. À la fin, l'ensemble des sous-totaux est additionné pour obtenir le prix négocié   
      final pour ce client. La patronne peut alors ajuster manuellement ce prix total   
      en fonction de la négociation avec le client.  
   5\. Enregistrement final et calcul automatique du chiffre d'affaires réel encaissé.

C. Système de Commission (Salaire Variable) :  
   \- Chaque pièce vendue attribue automatiquement une commission FIXE de 1 000 Ar   
     par défaut.  
   \- La Patronne dispose d'un menu secret lui permettant de modifier ce montant   
     de commission unitaire. Cette modification est GLOBALE et s'applique à toute   
     la boutique pour les ventes futures.  
   \- Le montant de commission en vigueur au moment de la vente est enregistré dans   
     la vente pour préserver l'historique (la commission unitaire utilisée est   
     copiée dans l'entité Vente).  
   \- La commission reste strictement fixe, peu importe la catégorie de l'article ou   
     le prix final négocié/discuté avec le client.  
   \- Exemple : Si un panier comporte 2 pièces (même vendues à un prix remisé de   
     28 000 Ar), le gain de l'employé reste de : 2 × 1 000 Ar \= 2 000 Ar.

D. Gestion des Avances (Comptabilité séparée des Dépenses) :  
   \- SÉPARATION STRICTE ENTRE "AVANCE" ET "DÉPENSE" : Le système doit traiter et   
     comptabiliser distinctement deux flux financiers totalement indépendants :  
     1\. Les Avances : Les sommes d'argent demandées par les employés (ex: avances   
        sur salaire). Elles sont déduites du salaire/commission de l'employé à la   
        fin de la journée.  
     2\. Les Dépenses de la boutique : Les frais liés exclusivement au fonctionnement   
        opérationnel de la boutique (achats de stock, factures, loyer, etc.).  
   \- Règle comptable : Le total des dépenses de la boutique ne doit pas inclure   
     les avances des employés. Ce sont deux entités comptables séparées.  
   \- Les demandes d'avances ne se font pas directement via l'application par l'employé.  
   \- Le processus se fait en face-à-face : l'employé demande physiquement l'avance   
     à la Patronne.  
   \- Une fois accordée, la Patronne enregistre manuellement le montant de l'avance   
     dans l'application, sous le profil de l'employé concerné.

E. Clôture, Réinitialisation et Ouverture de Journée :  
   \- Une "journée" n'est pas forcément liée à une date civile (une journée peut   
     être ouverte à 18h et clôturée à 3h du matin), mais une journée utilise   
     forcément une seule session le même jour. Une seule journée peut être ouverte   
     simultanément.  
   \- En fin de journée, la Patronne effectue la "validation finale des comptes".  
   \- Cette action archive instantanément l'intégralité des flux et ventes du jour.  
   \- Les compteurs de la journée en cours sont réinitialisés à zéro.  
   \- La Patronne a la flexibilité d'ouvrir immédiatement une nouvelle journée de   
     travail juste après la clôture, sans attendre le lendemain.  
   \- Formule de calcul automatique du solde journalier de l'employé :  
     Reste à payer du jour \= (Nombre total de pièces vendues par l'employé ×   
     Commission unitaire en vigueur) \- Total des avances perçues par l'employé

F. Flux de Caisse, Rapports et Historique Complet pour l'interface de la Patronne :  
   L'application offre un suivi chronologique précis des indicateurs suivants :  
   \- Liste des ventes de la journée (Heure, Quantités, Catégories de référence,   
     Prix réel facturé, Vendeur).  
   \- Chiffre d'Affaires Brut Réel (Total de l'argent liquide encaissé).  
   \- Dépenses de la boutique (Achats de fournitures, nourriture, frais divers) \-   
     excluant les avances employés.  
   \- Total des avances accordées aux employés.  
   \- Gains Réels / Bénéfices Nets du Jour (Argent réel encaissé \- Dépenses boutique \-   
     Commissions employés).  
   \- Statistiques de volume : Nombre d'articles vendus ventilés par catégorie de prix.

5\. CHARTE GRAPHIQUE & DESIGN INTERFACE (UI/UX)  
\-------------------------------------------------------------------------------  
\- Style général : Lumineux, épuré, simple, hautement intuitif et minimaliste.  
\- Mode : Uniquement mode clair (fonds clairs privilégiant la lisibilité sous le soleil).

Palette de couleurs officielle :  
\- Fond principal : Blanc pur (\#FFFFFF) & Gris ultra-clair (\#F8F9FA) pour structurer l'espace.  
\- Couleur Primaire (Validation, Succès, Entrées d'argent) : Vert émeraude (\#2ECC71).  
\- Couleur Secondaire (Informations, Sélection, Éléments interactifs) : Bleu moderne (\#3498DB).  
\- Couleur d'Alerte (Dépenses, Sorties d'argent, Avances) : Rouge corail (\#E74C3C).  
\- Texte & Contrastes : Gris anthracite / Foncé (\#2C3E50) pour une fatigue visuelle réduite.

\===============================================================================================  
ANALYSE FONCTIONNELLE ET ARCHITECTURE DES DONNÉES  
\===============================================================================================

\===============================================================================================  
DICTIONNAIRE DE DONNÉES COMPLET ET EXPLICATIONS DES ATTRIBUTS  
\===============================================================================================

\-------------------------------------------------------------------------------  
1\. ENTITÉ : UTILISATEUR  
\-------------------------------------------------------------------------------  
Description : Représente toute personne utilisant l'application.

\- idUtilisateur : Identifiant unique (Clé primaire) généré par le système pour   
  distinguer chaque utilisateur.  
\- nom           : Nom ou pseudonyme de la personne (ex: "Fara", "Patronne Ranja").  
\- codePin       : Code secret à 4 chiffres permettant à l'utilisateur de se   
  connecter rapidement sur l'application sans mot de passe complexe.  
\- role          : Niveau d'accès dans le système. Prend la valeur 'PATRONNE' (accès   
  total) ou 'EMPLOYE' (droits de consultation uniquement).  
\- actif         : Indicateur booléen (Vrai/Faux). Permet de désactiver un employé   
  qui a quitté la boutique pour l'empêcher de se connecter, sans supprimer ses   
  données historiques.  
\- dateCreation  : Date et heure de l'enregistrement de l'utilisateur dans le système.

\-------------------------------------------------------------------------------  
2\. ENTITÉ : JOURNÉE DE VENTE  
\-------------------------------------------------------------------------------  
Description : Représente une session de travail ouverte par la patronne.

\- idJournee        : Identifiant unique (Clé primaire) de la session journalière.  
\- heureOuverture   : Horodatage précis du moment où la patronne ouvre la journée.  
\- heureCloture     : Horodatage du moment où la patronne ferme la caisse.   
  Reste vide tant que la journée est active.  
\- statut           : État de la session. Vaut 'OUVERTE' (saisie possible) ou   
  'CLOTUREE' (données verrouillées en lecture seule).  
\- chiffreAffaires  : Montant total calculé. Somme des 'prixTotalFacture' de toutes   
  les ventes de cette journée.  
\- depensesBoutique : Montant total calculé. Somme de toutes les dépenses logistiques   
  et opérationnelles de la boutique saisies ce jour-là. N'inclut PAS les avances.  
\- beneficeNet      : Calcul comptable final : \[Chiffre d'Affaires\] \- \[Total des   
  Dépenses Boutique\].

\-------------------------------------------------------------------------------  
3\. ENTITÉ : CATÉGORIE DE PRIX  
\-------------------------------------------------------------------------------  
Description : Référence tarifaire utilisée lors des ventes.

\- idCategorie    : Identifiant unique (Clé primaire) de la catégorie tarifaire.  
\- libelle        : Nom ou désignation textuelle de la catégorie (ex: "Catégorie 1",   
  "Gamme Standard").  
\- prixReference  : Prix officiel en vigueur fixé par la patronne (ex: 3 000 Ar).   
  C'est la valeur par défaut proposée lors d'une vente.  
\- actif          : Indicateur (Vrai/Faux) permettant de masquer une catégorie qui   
  n'est plus commercialisée, sans casser l'historique des anciennes ventes.  
\- dateCreation   : Date d'enregistrement initial de cette catégorie de prix.

\-------------------------------------------------------------------------------  
4\. ENTITÉ : PARAMÈTRE DE COMMISSION  
\-------------------------------------------------------------------------------  
Description : Paramètre définissant le montant de commission par pièce vendue.

\- idParametre               : Identifiant unique (Clé primaire) de la règle de   
  commission.  
\- montantCommissionParPiece : Somme fixe attribuée au vendeur pour chaque unité   
  vendue (ex: 1 000 Ar). Modification globale applicable à toute la boutique.  
\- dateEffet                 : Date à partir de laquelle ce nouveau montant devient   
  applicable pour les ventes futures.  
\- modifiePar                : Identifiant de l'utilisateur (obligatoirement la   
  patronne) qui a mis à jour ce paramètre.

\-------------------------------------------------------------------------------  
5\. ENTITÉ : VENTE  
\-------------------------------------------------------------------------------  
Description : Représente une vente complète enregistrée par la patronne.

\- idVente                    : Identifiant unique (Clé primaire) de la transaction.  
\- dateHeure                  : Date et heure exactes de la validation de la vente.  
\- prixTotalFacture           : Montant final payé par le client (Somme des lignes   
  de vente associées, ajustée manuellement par la patronne).  
\- nombrePiecesTotal          : Quantité totale d'articles vendus dans cette transaction.  
\- commissionUnitaireUtilisee : Copie sécurisée du 'montantCommissionParPiece' en   
  vigueur au moment précis de cette vente.  
\- commissionTotale           : Calcul automatique : \[nombrePiecesTotal\] multiplié par   
  \[commissionUnitaireUtilisee\].  
\- vendeurId                  : Clé étrangère pointant vers l'UTILISATEUR (vendeur)   
  qui a réalisé la vente.  
\- journeeId                  : Clé étrangère reliant obligatoirement la vente à la   
  JOURNÉE DE VENTE active.  
\- statut                     : État de la vente (ex: 'VALIDEE', 'MODIFIEE', 'SUPPRIMEE').

\-------------------------------------------------------------------------------  
6\. ENTITÉ : LIGNE DE VENTE  
\-------------------------------------------------------------------------------  
Description : Représente un article ou groupe d'articles dans un panier.

\- idLigne                 : Identifiant unique (Clé primaire) de la ligne du panier.  
\- venteId                 : Clé étrangère rattachant cette ligne à sa VENTE principale.  
\- categorieId             : Clé étrangère identifiant la CATÉGORIE DE PRIX de l'article.  
\- prixReferenceHistorique : Copie sécurisée du prix de référence de la catégorie   
  au moment de la vente (permet de préserver l'historique même si la catégorie   
  est modifiée ultérieurement).  
\- quantite                : Nombre d'articles pour cette ligne.  
\- sousTotal               : Calcul automatique : \[prixReferenceHistorique\] × \[quantite\].

\-------------------------------------------------------------------------------  
7\. ENTITÉ : AVANCE EMPLOYÉ  
\-------------------------------------------------------------------------------  
Description : Représente une avance accordée à un employé.

\- idAvance        : Identifiant unique (Clé primaire) de l'avance.  
\- employeId       : Clé étrangère pointant vers l'UTILISATEUR (employé) bénéficiaire.  
\- montant         : Montant de l'avance accordée.  
\- dateHeure       : Date et heure d'enregistrement de l'avance.  
\- journeeId       : Clé étrangère reliant l'avance à la JOURNÉE DE VENTE.  
\- motif           : Texte facultatif justifiant l'avance.

\-------------------------------------------------------------------------------  
8\. ENTITÉ : DÉPENSE BOUTIQUE  
\-------------------------------------------------------------------------------  
Description : Représente une dépense liée au fonctionnement de la boutique.

\- idDepense       : Identifiant unique (Clé primaire) de la dépense.  
\- libelle         : Description de la dépense (ex: "Achat de fournitures").  
\- montant         : Montant de la dépense.  
\- dateHeure       : Date et heure d'enregistrement de la dépense.  
\- journeeId       : Clé étrangère reliant la dépense à la JOURNÉE DE VENTE.

\-------------------------------------------------------------------------------  
9\. ENTITÉ : JOURNAL D'AUDIT (LOG)  
\-------------------------------------------------------------------------------  
Description : Enregistre les actions sensibles pour traçabilité.

\- idLog           : Identifiant unique (Clé primaire) de l'entrée de log.  
\- action          : Type d'action (ex: 'SUPPRESSION\_VENTE', 'MODIFICATION\_COMMISSION').  
\- utilisateurId   : Identifiant de l'utilisateur ayant effectué l'action.  
\- dateHeure       : Date et heure de l'action.  
\- details         : Texte détaillant l'action (ex: motif de suppression, anciennes   
  et nouvelles valeurs).  
\- idEntiteConcernee : Identifiant de l'entité concernée (ex: idVente supprimée).

\===============================================================================================  
2\. MODÉLISATION DES FLUX  
\===============================================================================================

FLUX 1 : OUVERTURE D'UNE JOURNÉE  
Patronne  
→ Ouvrir une journée  
→ Création de la journée  
→ Statut \= OUVERTE  
→ Réinitialisation des compteurs

FLUX 2 : ENREGISTREMENT D'UNE VENTE  
Patronne  
→ Sélection d'un employé  
→ Création du panier  
→ Ajout de lignes de vente  
  → Sélection des catégories  
  → Saisie des quantités  
  → Calcul automatique des sous-totaux  
→ Addition de tous les sous-totaux  
→ Saisie manuelle du prix final négocié (ajustement global)  
→ Validation  
Le système :  
→ Crée la vente  
→ Crée les lignes de vente  
→ Calcule le nombre total de pièces  
→ Calcule la commission (basée sur le nombre de pièces)  
→ Enregistre la commission unitaire utilisée (copie historique)  
→ Met à jour les statistiques

FLUX 3 : MODIFICATION D'UNE VENTE  
Patronne  
→ Sélectionne une vente existante  
→ Modifie les lignes ou le prix final  
→ Valide la modification  
Le système :  
→ Recalcule le nombre total de pièces  
→ Recalcule la commission de l'employé concerné  
→ Recalcule le chiffre d'affaires  
→ Recalcule les statistiques globales  
→ Met à jour le statut de la vente en 'MODIFIEE'

FLUX 4 : SUPPRESSION D'UNE VENTE  
Patronne  
→ Sélectionne une vente existante  
→ Saisit un motif obligatoire  
→ Confirme la suppression  
Le système :  
→ Supprime la vente et ses lignes  
→ Enregistre l'action dans le journal d'audit (log) avec le motif  
→ Recalcule les statistiques globales  
→ Recalcule les commissions de l'employé concerné

FLUX 5 : SYNCHRONISATION TEMPS RÉEL  
Téléphone Patronne (serveur local)  
→ Modification des données  
→ Diffusion via Wi-Fi local  
→ Téléphones employés  
→ Mise à jour automatique de l'affichage

FLUX 6 : AVANCE EMPLOYÉ  
Employé  
→ Demande physique d'avance  
Patronne  
→ Accepte ou refuse  
Si accepté :  
→ Création d'une avance dans l'entité AVANCE\_EMPLOYE  
→ Mise à jour du solde de l'employé (déduction future)

FLUX 7 : DÉPENSE BOUTIQUE  
Patronne  
→ Saisie d'une dépense (ne concerne que le fonctionnement de la boutique)  
Le système :  
→ Crée une dépense boutique  
→ Met à jour le bénéfice net (N'impacte PAS les commissions des employés)

FLUX 8 : CLÔTURE D'UNE JOURNÉE  
Patronne  
→ Validation finale  
Le système :  
→ Calcule les résultats définitifs  
  → Chiffre d'affaires total  
  → Total des dépenses boutique  
  → Total des avances accordées  
  → Total des commissions brutes  
  → Bénéfice net  
→ Archive les données  
→ Clôture la journée (statut \= CLOTUREE)  
Patronne  
→ Peut ouvrir immédiatement une nouvelle journée

\===============================================================================================  
3\. CONTRAINTES ET RÈGLES DE GESTION  
\===============================================================================================

RÈGLES DE SÉCURITÉ  
\--------------------------------------------------------------------------------  
R1. Seule la patronne peut :  
    \- créer une vente  
    \- modifier une vente (avec recalcul automatique)  
    \- supprimer une vente (avec motif obligatoire et journalisation)  
    \- gérer les catégories de prix  
    \- gérer le paramètre de commission (modification globale)  
    \- enregistrer des dépenses boutique  
    \- enregistrer des avances employés  
    \- clôturer une journée

R2. Un employé peut consulter uniquement :  
    \- ses propres ventes  
    \- ses propres commissions  
    \- ses propres avances  
    \- son résumé journalier  
    \- le nombre total de ventes (quantité de pièces) des collègues (sans le CA)

R3. Un employé ne peut jamais consulter :  
    \- les chiffres d'affaires des autres employés  
    \- les commissions des autres employés  
    \- les dépenses de la boutique

R4. L'authentification se fait par code PIN à 4 chiffres.  
    Chaque utilisateur peut modifier son propre code PIN depuis les paramètres.  
    Aucune procédure de récupération en V1 (réinitialisation manuelle par la patronne).

RÈGLES MÉTIER  
\--------------------------------------------------------------------------------  
R5. Une vente doit obligatoirement être liée :  
    \- à une journée (OUVERTE)  
    \- à un vendeur (employé actif)

R6. Une ligne de vente ne peut pas exister sans vente.  
    Relation : VENTE (1) → (N) LIGNE\_VENTE

R7. Une seule journée peut être ouverte simultanément.

R8. Une catégorie supprimée doit être désactivée (actif \= false) et non supprimée   
    physiquement.

R9. Une vente clôturée (journée CLOTUREE) devient définitivement immuable.  
    Toute modification ou suppression est interdite après clôture.

R10. La commission est calculée uniquement sur le nombre de pièces.  
     Formule : Commission \= Nombre de pièces × Commission unitaire en vigueur  
     Le prix de vente n'a aucun impact sur la commission.

R11. Le montant de la commission unitaire utilisé lors d'une vente doit être   
     enregistré dans la vente (commissionUnitaireUtilisee) pour préserver l'historique.

R12. Le prix de référence utilisé lors d'une vente doit être enregistré dans la   
     ligne de vente (prixReferenceHistorique) pour préserver l'historique.

R13. Calcul du reste à payer pour un employé en fin de journée :  
     Reste à payer \= Commission brute totale \- Avances reçues totales

R14. Les avances et les dépenses boutique sont deux flux financiers totalement   
     séparés et indépendants :  
     \- Les avances sont déduites du salaire/commission des employés.  
     \- Les dépenses boutique impactent le bénéfice net mais pas les commissions.

R15. Lors d'une modification de vente, le système doit automatiquement :  
     \- Recalculer le nombre total de pièces  
     \- Recalculer la commission de l'employé concerné  
     \- Recalculer le chiffre d'affaires  
     \- Recalculer toutes les statistiques globales

R16. Lors d'une suppression de vente :  
     \- Un motif est obligatoirement requis  
     \- L'action est enregistrée dans le journal d'audit (log)  
     \- Le système recalcule toutes les statistiques et commissions

R17. Une journée peut être ouverte à n'importe quelle heure, indépendamment de la   
     date civile. Une seule session est autorisée par jour (au sens date calendaire   
     pour la clôture).

R18. La modification du paramètre de commission est globale et s'applique à toute   
     la boutique pour toutes les ventes futures. Les ventes passées conservent la   
     commission qui était en vigueur à leur date d'enregistrement.

Contrainte technique :   
C’est une application conçue pour android entre version 11 et 5

FICHE TECHNIQUE PROJET BOUTIQUE LOCAL \- VERSION SYNTHETIQUE  
\===============================================================================================

1\. STACK TECHNIQUE UNIQUE (SELECTION FINALE)  
\-------------------------------------------------------------------------------  
\- Frontend : Kotlin \+ Jetpack Compose (Android 5 a 11, API 21 a 30\)  
\- Backend : Ktor Server embarqué dans l'app Patronne  
\- Base de donnees : Room (SQLite) sur chaque appareil \+ H2 pour le serveur  
\- Communication : WebSockets (temps reel) \+ REST API (CRUD) sur HTTP local  
\- Gestion d'etat : Android ViewModel \+ StateFlow  
\- Build : Gradle (Kotlin DSL)