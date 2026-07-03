package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.DepenseBoutique
import kotlinx.coroutines.flow.Flow

@Dao
interface DepenseBoutiqueDao {

    @Insert
    suspend fun insert(depense: DepenseBoutique): Long

    @Query("SELECT * FROM depenses_boutique WHERE journeeId = :journeeId")
    fun getDepensesByJournee(journeeId: Long): Flow<List<DepenseBoutique>>

    @Query("SELECT SUM(montant) FROM depenses_boutique WHERE journeeId = :journeeId")
    suspend fun getTotalDepenses(journeeId: Long): Long?

    @Query("SELECT SUM(montant) FROM depenses_boutique WHERE journeeId = :journeeId")
    fun observeTotalDepenses(journeeId: Long): Flow<Long?>
}