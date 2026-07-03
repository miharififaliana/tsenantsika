package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.ParametreCommission
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import com.example.tsenantsika.network.models.WsEventType
import kotlinx.coroutines.flow.Flow

class ParametreCommissionRepository(
    database: BoutiqueDatabase,
    private val eventBus: EventBus
) : BaseRepository(database) {

    private val dao = database.parametreCommissionDao()

    suspend fun insert(parametre: ParametreCommission): Long = safeCall {
        val id = dao.insert(parametre)
        eventBus.broadcast(WsEvent(WsEventType.PARAMETRE_COMMISSION_MODIFIE, id.toString()))
        id
    }

    suspend fun getDernierParametre(): ParametreCommission? = safeCall { dao.getDernierParametre() }
    fun getHistorique(): Flow<List<ParametreCommission>> = dao.getHistorique()
}
