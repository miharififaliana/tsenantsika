package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.DepenseBoutique
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import com.example.tsenantsika.network.models.WsEventType
import kotlinx.coroutines.flow.Flow

class DepenseBoutiqueRepository(
    database: BoutiqueDatabase,
    private val eventBus: EventBus
) : BaseRepository(database) {

    private val dao = database.depenseBoutiqueDao()

    suspend fun insert(depense: DepenseBoutique): Long = safeCall { dao.insert(depense) }

    suspend fun addDepense(depense: DepenseBoutique, utilisateurId: Long): Long = safeCall {
        val id = dao.insert(depense)
        eventBus.broadcast(WsEvent(WsEventType.DEPENSE_AJOUTEE, id.toString()))
        id
    }

    fun getDepensesByJournee(journeeId: Long): Flow<List<DepenseBoutique>> = dao.getDepensesByJournee(journeeId)

    suspend fun getTotalDepenses(journeeId: Long): Long? = safeCall {
        dao.getTotalDepenses(journeeId)
    }
}