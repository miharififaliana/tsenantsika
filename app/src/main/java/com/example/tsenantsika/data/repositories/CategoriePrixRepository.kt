package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.CategoriePrix
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import com.example.tsenantsika.network.models.WsEventType
import kotlinx.coroutines.flow.Flow

class CategoriePrixRepository(
    database: BoutiqueDatabase,
    private val eventBus: EventBus
) : BaseRepository(database) {

    private val dao = database.categoriePrixDao()

    suspend fun insert(categorie: CategoriePrix): Long = safeCall {
        val id = dao.insert(categorie)
        eventBus.broadcast(WsEvent(WsEventType.CATEGORIE_MODIFIEE, id.toString()))
        id
    }

    suspend fun update(categorie: CategoriePrix) = safeCall {
        dao.update(categorie)
        eventBus.broadcast(WsEvent(WsEventType.CATEGORIE_MODIFIEE, categorie.idCategorie.toString()))
    }

    fun getCategoriesActives(): Flow<List<CategoriePrix>> = dao.getCategoriesActives()
    fun getAll(): Flow<List<CategoriePrix>> = dao.getAll()
}
