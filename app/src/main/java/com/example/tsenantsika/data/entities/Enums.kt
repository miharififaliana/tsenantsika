package com.example.tsenantsika.data.entities

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    PATRONNE,
    EMPLOYE
}

/**
 * Enum pour le statut d'une journée de vente.
 * OUVERTE : Permet les saisies (ventes, avances, dépenses).
 * CLOTUREE : Lecture seule, données archivées et immuables (R9).
 */
@Serializable
enum class StatutJournee {
    OUVERTE,
    CLOTUREE
}

/**
 * Enum pour le statut d'une vente (étendu pour couvrir tous les cas du CdC).
 * VALIDEE : Vente normale.
 * MODIFIEE : Après modification par la Patronne (avec recalcul).
 * SUPPRIMEE : Après suppression logique avec motif d'audit.
 */
@Serializable
enum class StatutVente {
    VALIDEE,
    MODIFIEE,
    SUPPRIMEE
}

/**
 * Enum pour les actions d'audit dans le Journal d'Audit.
 * Couvre toutes les actions sensibles mentionnées (suppressions, modifications de paramètres, etc.).
 */
enum class ActionAudit {
    CONNEXION,
    DECONNEXION,
    AJOUT_VENTE,
    MODIFICATION_VENTE,
    SUPPRESSION_VENTE,
    AJOUT_AVANCE,
    AJOUT_DEPENSE,
    MODIFICATION_COMMISSION,
    MODIFICATION_CATEGORIE,
    OUVERTURE_JOURNEE,
    CLOTURE_JOURNEE,
    CREATION_EMPLOYE,
    DESACTIVATION_EMPLOYE,
    // Ajouts futurs possibles sans casser la compatibilité
}

/**
 * TypeConverters pour Room (stockage en String pour lisibilité, robustesse et évolutivité).
 * À enregistrer dans BoutiqueDatabase via @TypeConverters(Converters::class).
 */
class Converters {

    @TypeConverter
    fun fromRole(role: Role?): String? = role?.name

    @TypeConverter
    fun toRole(value: String?): Role? = value?.let { Role.valueOf(it) }

    @TypeConverter
    fun fromStatutJournee(statut: StatutJournee?): String? = statut?.name

    @TypeConverter
    fun toStatutJournee(value: String?): StatutJournee? = value?.let { StatutJournee.valueOf(it) }

    @TypeConverter
    fun fromStatutVente(statut: StatutVente?): String? = statut?.name

    @TypeConverter
    fun toStatutVente(value: String?): StatutVente? = value?.let { StatutVente.valueOf(it) }

    @TypeConverter
    fun fromActionAudit(action: ActionAudit?): String? = action?.name

    @TypeConverter
    fun toActionAudit(value: String?): ActionAudit? = value?.let { ActionAudit.valueOf(it) }
}