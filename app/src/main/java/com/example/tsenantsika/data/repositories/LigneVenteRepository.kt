package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.LigneVente

class LigneVenteRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.ligneVenteDao()

    suspend fun insert(ligne: LigneVente): Long = safeCall { dao.insert(ligne) }

    suspend fun insertAll(lignes: List<LigneVente>) = safeCall { dao.insertAll(lignes) }

    suspend fun getLignesByVente(venteId: Long): List<LigneVente> = safeCall {
        dao.getLignesByVente(venteId)
    }

    suspend fun deleteByVenteId(venteId: Long): Int = safeCall {
        dao.deleteLignesByVenteId(venteId)
    }
}