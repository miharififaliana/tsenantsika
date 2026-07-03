package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import kotlinx.serialization.Serializable

@Entity(
    tableName = "ventes",
    foreignKeys = [
        ForeignKey(
            entity = Utilisateur::class,
            parentColumns = ["idUtilisateur"],
            childColumns = ["vendeurId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Journee::class,
            parentColumns = ["idJournee"],
            childColumns = ["journeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["vendeurId"]),
        Index(value = ["journeeId"]),
        Index(value = ["dateHeure"]),
        Index(value = ["journeeId", "vendeurId"])
    ]
)
@Serializable
data class Vente(
    @PrimaryKey(autoGenerate = true)
    val idVente: Long = 0,

    @Serializable(with = InstantAsLongSerializer::class)
    val dateHeure: Instant = Instant.now(),

    val prixTotalFacture: Long,

    val nombrePiecesTotal: Int,

    val commissionUnitaireUtilisee: Long,

    val commissionTotale: Long,

    val vendeurId: Long,

    val journeeId: Long,

    val statut: StatutVente = StatutVente.VALIDEE
)