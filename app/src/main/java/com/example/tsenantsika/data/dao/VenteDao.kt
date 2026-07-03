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

    @Query("SELECT * FROM ventes WHERE journeeId = :journeeId ORDER BY dateHeure DESC")
    suspend fun getVentesSync(journeeId: Long): List<Vente>

    @Transaction
    @Query("SELECT * FROM ventes WHERE vendeurId = :vendeurId AND journeeId = :journeeId")
    fun getVentesByVendeur(journeeId: Long, vendeurId: Long): Flow<List<Vente>>

    @Query("SELECT SUM(prixTotalFacture) FROM ventes WHERE journeeId = :journeeId")
    suspend fun getChiffreAffairesJournee(journeeId: Long): Long?

    @Query("SELECT SUM(commissionTotale) FROM ventes WHERE vendeurId = :vendeurId AND journeeId = :journeeId")
    suspend fun getCommissionBruteVendeur(journeeId: Long, vendeurId: Long): Long?

    @Query("SELECT SUM(commissionTotale) FROM ventes WHERE journeeId = :journeeId")
    suspend fun getTotalCommissionsJournee(journeeId: Long): Long?

    @Query("SELECT SUM(nombrePiecesTotal) FROM ventes WHERE journeeId = :journeeId")
    suspend fun getTotalPiecesJournee(journeeId: Long): Int?

    @Query("SELECT SUM(nombrePiecesTotal) FROM ventes WHERE journeeId = :journeeId AND vendeurId = :vendeurId")
    suspend fun getTotalPiecesVendeur(journeeId: Long, vendeurId: Long): Int?

    @Query("SELECT SUM(nombrePiecesTotal) FROM ventes WHERE journeeId = :journeeId AND vendeurId != :vendeurId")
    suspend fun getTotalPiecesAutresVendeurs(journeeId: Long, vendeurId: Long): Int?

    @Query("SELECT COUNT(*) FROM ventes WHERE journeeId = :journeeId")
    suspend fun countVentesJournee(journeeId: Long): Int

    @Query("SELECT * FROM ventes WHERE idVente = :id")
    suspend fun getById(id: Long): Vente?

    @Query("SELECT SUM(prixTotalFacture) FROM ventes WHERE journeeId = :journeeId")
    fun observeChiffreAffaires(journeeId: Long): Flow<Long?>

    @Query("SELECT SUM(commissionTotale) FROM ventes WHERE journeeId = :journeeId")
    fun observeTotalCommissions(journeeId: Long): Flow<Long?>

    @Query("SELECT SUM(nombrePiecesTotal) FROM ventes WHERE journeeId = :journeeId")
    fun observeTotalPieces(journeeId: Long): Flow<Int?>

    @Query("SELECT COUNT(*) FROM ventes WHERE journeeId = :journeeId")
    fun observeCountVentes(journeeId: Long): Flow<Int>
}