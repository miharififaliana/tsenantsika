package com.example.tsenantsika.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tsenantsika.data.entities.*
import com.example.tsenantsika.data.dao.*
import com.example.tsenantsika.data.dao.VenteDao
// Ajoutez cet import s'il manque
import com.example.tsenantsika.data.dao.LigneVenteDao

/**
 * Classe principale de la base de données Room pour l'application Boutique.
 * Version 1 initiale conformément à la Roadmap Phase 1.
 * Inclut toutes les entités du dictionnaire de données du Cahier des Charges.
 *
 * Gestion des converters pour enums et dates (Instant).
 * exportSchema = true pour faciliter les migrations futures et l'inspection.
 */
@Database(
    entities = [
        Utilisateur::class,
        Journee::class,
        CategoriePrix::class,
        ParametreCommission::class,
        Vente::class,
        LigneVente::class,
        AvanceEmploye::class,
        DepenseBoutique::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    Converters::class,
    InstantConverter::class
)
abstract class BoutiqueDatabase : RoomDatabase() {

    // Déclarations des DAOs (implémentés dans la sous-tâche suivante)
    abstract fun utilisateurDao(): UtilisateurDao
    abstract fun journeeDao(): JourneeDao
    abstract fun categoriePrixDao(): CategoriePrixDao
    abstract fun parametreCommissionDao(): ParametreCommissionDao
    abstract fun venteDao(): VenteDao
    abstract fun ligneVenteDao(): LigneVenteDao
    abstract fun avanceEmployeDao(): AvanceEmployeDao
    abstract fun depenseBoutiqueDao(): DepenseBoutiqueDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        const val DATABASE_NAME = "boutique_database.db"
    }
}