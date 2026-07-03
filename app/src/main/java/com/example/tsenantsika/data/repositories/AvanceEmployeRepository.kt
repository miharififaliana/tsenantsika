package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.AvanceEmploye
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import com.example.tsenantsika.network.models.WsEventType
import kotlinx.coroutines.flow.Flow

class AvanceEmployeRepository(
    database: BoutiqueDatabase,
    private val eventBus: EventBus
) : BaseRepository(database) {

    private val dao = database.avanceEmployeDao()

    suspend fun insert(avance: AvanceEmploye): Long = safeCall { dao.insert(avance) }

    suspend fun addAvance(avance: AvanceEmploye, utilisateurId: Long): Long = safeCall {
        val id = dao.insert(avance)
        eventBus.broadcast(WsEvent(WsEventType.AVANCE_AJOUTEE, id.toString()))
        id
    }

    fun getAvancesByJournee(journeeId: Long): Flow<List<AvanceEmploye>> = dao.getAvancesByJournee(journeeId)

    suspend fun getTotalAvancesEmploye(journeeId: Long, employeId: Long): Long? = safeCall {
        dao.getTotalAvancesEmploye(journeeId, employeId)
    }
}