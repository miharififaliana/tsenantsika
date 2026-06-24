package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.Vente
import kotlinx.coroutines.flow.Flow

@Dao
interface VenteDao {

    @Insert
    suspend fun insert(vente: Vente): Long

    @Update
    suspend fun update(vente: Vente)

    @Delete
    suspend fun delete(vente: Vente)

    @Transaction
    @Query("SELECT * FROM ventes WHERE journeeId = :journeeId ORDER BY dateHeure DESC")
    fun getVentesByJournee(journeeId: Long): Flow<List<Vente>>

    @Transaction
    @Query("SELECT * FROM ventes WHERE vendeurId = :vendeurId AND journeeId = :journeeId")
    fun getVentesByVendeur(journeeId: Long, vendeurId: Long): Flow<List<Vente>>

    @Query("SELECT SUM(prixTotalFacture) FROM ventes WHERE journeeId = :journeeId")
    suspend fun getChiffreAffairesJournee(journeeId: Long): Long?

    @Query("SELECT SUM(commissionTotale) FROM ventes WHERE vendeurId = :vendeurId AND journeeId = :journeeId")
    suspend fun getCommissionBruteVendeur(journeeId: Long, vendeurId: Long): Long?
}