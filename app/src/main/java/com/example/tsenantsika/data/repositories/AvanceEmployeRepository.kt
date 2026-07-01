package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.AvanceEmploye
import kotlinx.coroutines.flow.Flow

class AvanceEmployeRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.avanceEmployeDao()

    suspend fun insert(avance: AvanceEmploye): Long = safeCall { dao.insert(avance) }

    fun getAvancesByJournee(journeeId: Long): Flow<List<AvanceEmploye>> = dao.getAvancesByJournee(journeeId)

    suspend fun getTotalAvancesEmploye(journeeId: Long, employeId: Long): Long? = safeCall {
        dao.getTotalAvancesEmploye(journeeId, employeId)
    }
}