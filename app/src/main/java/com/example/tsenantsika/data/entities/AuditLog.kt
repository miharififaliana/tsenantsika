package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Journal d'audit immuable pour traçabilité.
 */
@Entity(
    tableName = "audit_logs",
    foreignKeys = [
        ForeignKey(
            entity = Utilisateur::class,
            parentColumns = ["idUtilisateur"],
            childColumns = ["utilisateurId"],
            onDelete = ForeignKey.SET_NULL // ou RESTRICT
        )
    ],
    indices = [Index("action"), Index("dateHeure"), Index("utilisateurId")]
)
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val idLog: Long = 0,

    val action: ActionAudit,

    val utilisateurId: Long?,

    val dateHeure: Instant = Instant.now(),

    val details: String,

    val idEntiteConcernee: String? = null // ex: "vente_123"
)