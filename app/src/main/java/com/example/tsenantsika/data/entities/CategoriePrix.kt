package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Entité Catégorie de Prix (4 catégories initiales + admin).
 */
@Entity(
    tableName = "categories_prix",
    indices = [Index(value = ["libelle"], unique = true)]
)
data class CategoriePrix(
    @PrimaryKey(autoGenerate = true)
    val idCategorie: Long = 0,

    val libelle: String,

    val prixReference: Long, // en Ariary (Long pour montants)

    val actif: Boolean = true,

    val dateCreation: Instant = Instant.now()
)