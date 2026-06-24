package com.example.tsenantsika.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Historique des paramètres de commission (un seul actif en vigueur).
 */
@Entity(tableName = "parametres_commission")
data class ParametreCommission(
    @PrimaryKey(autoGenerate = true)
    val idParametre: Long = 0,

    val montantCommissionParPiece: Long, // ex: 1000 Ar

    val dateEffet: Instant = Instant.now(),

    val modifiePar: Long // idUtilisateur (Patronne)
)