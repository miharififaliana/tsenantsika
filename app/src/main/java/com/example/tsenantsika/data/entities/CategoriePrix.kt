package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import kotlinx.serialization.Serializable

@Entity(
    tableName = "categories_prix",
    indices = [Index(value = ["libelle"], unique = true)]
)
@Serializable
data class CategoriePrix(
    @PrimaryKey(autoGenerate = true)
    val idCategorie: Long = 0,

    val libelle: String,

    val prixReference: Long, // en Ariary (Long pour montants)

    val actif: Boolean = true,

    @Serializable(with = InstantAsLongSerializer::class)
    val dateCreation: Instant = Instant.now()
)