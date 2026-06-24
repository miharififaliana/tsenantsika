package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Dépenses opérationnelles de la boutique (exclut avances).
 */
@Entity(
    tableName = "depenses_boutique",
    foreignKeys = [
        ForeignKey(
            entity = Journee::class,
            parentColumns = ["idJournee"],
            childColumns = ["journeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("journeeId")]
)
data class DepenseBoutique(
    @PrimaryKey(autoGenerate = true)
    val idDepense: Long = 0,

    val libelle: String,

    val montant: Long,

    val dateHeure: Instant = Instant.now(),

    val journeeId: Long
)