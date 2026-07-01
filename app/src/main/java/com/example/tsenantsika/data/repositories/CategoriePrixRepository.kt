package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.CategoriePrix
import kotlinx.coroutines.flow.Flow

class CategoriePrixRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.categoriePrixDao()

    suspend fun insert(categorie: CategoriePrix): Long = safeCall { dao.insert(categorie) }

    suspend fun update(categorie: CategoriePrix) = safeCall { dao.update(categorie) }

    fun getCategoriesActives(): Flow<List<CategoriePrix>> = dao.getCategoriesActives()
}