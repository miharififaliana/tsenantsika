package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.AvanceEmploye
import kotlinx.coroutines.flow.Flow

@Dao
interface AvanceEmployeDao {

    @Insert
    suspend fun insert(avance: AvanceEmploye): Long

    @Query("SELECT * FROM avances_employe WHERE journeeId = :journeeId")
    fun getAvancesByJournee(journeeId: Long): Flow<List<AvanceEmploye>>

    @Query("SELECT SUM(montant) FROM avances_employe WHERE employeId = :employeId AND journeeId = :journeeId")
    suspend fun getTotalAvancesEmploye(journeeId: Long, employeId: Long): Long?
}