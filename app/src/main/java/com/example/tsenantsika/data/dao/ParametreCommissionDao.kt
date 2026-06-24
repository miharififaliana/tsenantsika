package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.ParametreCommission
import kotlinx.coroutines.flow.Flow

@Dao
interface ParametreCommissionDao {

    @Insert
    suspend fun insert(parametre: ParametreCommission): Long

    @Query("SELECT * FROM parametres_commission ORDER BY dateEffet DESC LIMIT 1")
    suspend fun getDernierParametre(): ParametreCommission?

    @Query("SELECT * FROM parametres_commission ORDER BY dateEffet DESC")
    fun getHistorique(): Flow<List<ParametreCommission>>
}