package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Entité Journée de Vente (une seule OUVERTE à la fois - R7).
 */
@Entity(
    tableName = "journees",
    indices = [Index(value = ["statut"])]
)
data class Journee(
    @PrimaryKey(autoGenerate = true)
    val idJournee: Long = 0,

    val heureOuverture: Instant,

    val heureCloture: Instant? = null,

    val statut: StatutJournee = StatutJournee.OUVERTE,

    val chiffreAffaires: Long = 0,

    val depensesBoutique: Long = 0,

    val beneficeNet: Long = 0
)