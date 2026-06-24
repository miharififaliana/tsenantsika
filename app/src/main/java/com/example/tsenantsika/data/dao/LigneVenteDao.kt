package com.example.tsenantsika.data.dao
import androidx.room.*
import com.example.tsenantsika.data.entities.LigneVente

@Dao
interface LigneVenteDao {

    @Insert
    suspend fun insert(ligne: LigneVente): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lignes: List<LigneVente>)

    @Query("SELECT * FROM lignes_vente WHERE venteId = :venteId")
    suspend fun getLignesByVente(venteId: Long): List<LigneVente>

    /**
     * Supprime toutes les lignes associées à une vente.
     * Utilisé lors de la modification ou suppression d'une vente.
     */
    @Query("DELETE FROM lignes_vente WHERE venteId = :venteId")
    suspend fun deleteLignesByVenteId(venteId: Long): Int
}