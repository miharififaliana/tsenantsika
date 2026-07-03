package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import kotlinx.serialization.Serializable

@Entity(tableName = "journees", indices = [Index(value = ["statut"])])
@Serializable
data class Journee(
    @PrimaryKey(autoGenerate = true)
    val idJournee: Long = 0,

    @Serializable(with = InstantAsLongSerializer::class)
    val heureOuverture: Instant,

    @Serializable(with = InstantAsLongSerializer::class)
    val heureCloture: Instant? = null,

    val statut: StatutJournee = StatutJournee.OUVERTE,

    val chiffreAffaires: Long = 0,

    val depensesBoutique: Long = 0,

    val beneficeNet: Long = 0
)