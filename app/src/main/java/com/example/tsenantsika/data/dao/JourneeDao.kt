package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.Journee
import com.example.tsenantsika.data.entities.StatutJournee
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneeDao {

    @Insert
    suspend fun insert(journee: Journee): Long

    @Update
    suspend fun update(journee: Journee)

    @Query("SELECT * FROM journees WHERE statut = :statut LIMIT 1")
    suspend fun getJourneeOuverte(statut: StatutJournee = StatutJournee.OUVERTE): Journee?

    @Query("SELECT * FROM journees ORDER BY heureOuverture DESC")
    fun getHistorique(): Flow<List<Journee>>

    @Query("SELECT * FROM journees WHERE idJournee = :id")
    suspend fun getById(id: Long): Journee?
}