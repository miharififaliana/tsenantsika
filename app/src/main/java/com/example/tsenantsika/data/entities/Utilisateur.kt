package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import kotlinx.serialization.Serializable

@Entity(
    tableName = "utilisateurs",
    indices = [
        Index(value = ["nom"], unique = true),
        Index(value = ["role"]),
        Index(value = ["actif"])
    ]
)
@Serializable
data class Utilisateur(
    @PrimaryKey(autoGenerate = true)
    val idUtilisateur: Long = 0,

    val nom: String,

    val codePin: String, // 4 chiffres, stocké hashé en prod (ici clair pour simplicité V1)

    val role: Role,

    val actif: Boolean = true,

    @Serializable(with = InstantAsLongSerializer::class)
    val dateCreation: Instant = Instant.now()
)