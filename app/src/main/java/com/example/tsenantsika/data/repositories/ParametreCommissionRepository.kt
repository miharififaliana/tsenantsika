package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.ParametreCommission
import kotlinx.coroutines.flow.Flow

class ParametreCommissionRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.parametreCommissionDao()

    suspend fun insert(parametre: ParametreCommission): Long = safeCall { dao.insert(parametre) }

    suspend fun getDernierParametre(): ParametreCommission? = safeCall { dao.getDernierParametre() }

    fun getHistorique(): Flow<List<ParametreCommission>> = dao.getHistorique()
}