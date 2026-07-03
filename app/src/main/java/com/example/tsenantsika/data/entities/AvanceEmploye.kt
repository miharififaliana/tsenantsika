package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Avances aux employés (séparées des dépenses boutique).
 */
@Entity(
    tableName = "avances_employe",
    foreignKeys = [
        ForeignKey(
            entity = Utilisateur::class,
            parentColumns = ["idUtilisateur"],
            childColumns = ["employeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Journee::class,
            parentColumns = ["idJournee"],
            childColumns = ["journeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("employeId"), Index("journeeId"), Index(value = ["journeeId", "employeId"])]
)
data class AvanceEmploye(
    @PrimaryKey(autoGenerate = true)
    val idAvance: Long = 0,

    val employeId: Long,

    val montant: Long,

    val dateHeure: Instant = Instant.now(),

    val journeeId: Long,

    val motif: String? = null
)