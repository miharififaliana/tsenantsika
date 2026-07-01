package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.Journee
import kotlinx.coroutines.flow.Flow

class JourneeRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.journeeDao()

    suspend fun insert(journee: Journee): Long = safeCall { dao.insert(journee) }

    suspend fun update(journee: Journee) = safeCall { dao.update(journee) }

    suspend fun getJourneeOuverte(): Journee? = safeCall { dao.getJourneeOuverte() }

    fun getHistorique(): Flow<List<Journee>> = dao.getHistorique()
}