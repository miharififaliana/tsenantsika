package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.Vente
import kotlinx.coroutines.flow.Flow

class VenteRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val venteDao = database.venteDao()
    private val ligneDao = database.ligneVenteDao()

    suspend fun createVenteAvecLignes(vente: Vente, lignes: List<com.example.tsenantsika.data.entities.LigneVente>): Long = withTransaction {
        val venteId = venteDao.insert(vente)
        lignes.forEach { it.venteId = venteId.toLong() } // Mise à jour de la FK
        ligneDao.insertAll(lignes)
        venteId
    }

    fun getVentesByJournee(journeeId: Long): Flow<List<Vente>> = venteDao.getVentesByJournee(journeeId)

    suspend fun getChiffreAffairesJournee(journeeId: Long): Long? = safeCall {
        venteDao.getChiffreAffairesJournee(journeeId)
    }
}