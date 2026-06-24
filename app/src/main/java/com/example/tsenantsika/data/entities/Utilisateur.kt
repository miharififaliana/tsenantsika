package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Entité Utilisateur selon le dictionnaire du CdC.
 * Représente Patronne et Employés.
 */
@Entity(
    tableName = "utilisateurs",
    indices = [
        Index(value = ["nom"], unique = true),
        Index(value = ["role"]),
        Index(value = ["actif"])
    ]
)
data class Utilisateur(
    @PrimaryKey(autoGenerate = true)
    val idUtilisateur: Long = 0,

    val nom: String,

    val codePin: String, // 4 chiffres, stocké hashé en prod (ici clair pour simplicité V1)

    val role: Role,

    val actif: Boolean = true,

    val dateCreation: Instant = Instant.now()
)