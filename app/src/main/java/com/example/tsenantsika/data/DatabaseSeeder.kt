package com.example.tsenantsika.data

import com.example.tsenantsika.data.entities.CategoriePrix
import com.example.tsenantsika.data.entities.ParametreCommission
import com.example.tsenantsika.data.repositories.CategoriePrixRepository
import com.example.tsenantsika.data.repositories.ParametreCommissionRepository
import java.time.Instant

object DatabaseSeeder {

    suspend fun seedDefaults(
        categorieRepo: CategoriePrixRepository,
        commissionRepo: ParametreCommissionRepository,
        patronneId: Long
    ) {
        if (commissionRepo.getDernierParametre() == null) {
            commissionRepo.insert(
                ParametreCommission(
                    montantCommissionParPiece = 1000,
                    dateEffet = Instant.now(),
                    modifiePar = patronneId
                )
            )
        }
        listOf(
            CategoriePrix(libelle = "Catégorie 1", prixReference = 3_000),
            CategoriePrix(libelle = "Catégorie 2", prixReference = 15_000),
            CategoriePrix(libelle = "Catégorie 3", prixReference = 25_000),
            CategoriePrix(libelle = "Catégorie 4", prixReference = 35_000)
        ).forEach { cat ->
            runCatching { categorieRepo.insert(cat) }
        }
    }
}
