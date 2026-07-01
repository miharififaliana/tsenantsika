package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Ligne d'un panier (relation 1 Vente → N Lignes).
 */
@Entity(
    tableName = "lignes_vente",
    foreignKeys = [
        ForeignKey(
            entity = Vente::class,
            parentColumns = ["idVente"],
            childColumns = ["venteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriePrix::class,
            parentColumns = ["idCategorie"],
            childColumns = ["categorieId"],
            onDelete = ForeignKey.RESTRICT // Préserve historique
        )
    ],
    indices = [Index(value = ["venteId"]), Index(value = ["categorieId"])]
)
data class LigneVente(
    @PrimaryKey(autoGenerate = true)
    val idLigne: Long = 0L,

    var venteId: Long = 0L,

    val categorieId: Long,

    val prixReferenceHistorique: Long,

    val quantite: Int,

    val sousTotal: Long
)