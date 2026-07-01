package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.DepenseBoutique
import kotlinx.coroutines.flow.Flow

class DepenseBoutiqueRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.depenseBoutiqueDao()

    suspend fun insert(depense: DepenseBoutique): Long = safeCall { dao.insert(depense) }

    fun getDepensesByJournee(journeeId: Long): Flow<List<DepenseBoutique>> = dao.getDepensesByJournee(journeeId)

    suspend fun getTotalDepenses(journeeId: Long): Long? = safeCall {
        dao.getTotalDepenses(journeeId)
    }
}