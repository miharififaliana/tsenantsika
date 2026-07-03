package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.Role
import com.example.tsenantsika.data.entities.Utilisateur
import kotlinx.coroutines.flow.Flow

@Dao
interface UtilisateurDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(utilisateur: Utilisateur): Long

    @Update
    suspend fun update(utilisateur: Utilisateur)

    @Delete
    suspend fun delete(utilisateur: Utilisateur)

    @Query("SELECT * FROM utilisateurs WHERE idUtilisateur = :id")
    suspend fun getById(id: Long): Utilisateur?

    @Query("SELECT * FROM utilisateurs WHERE nom = :nom AND codePin = :codePin AND actif = 1 LIMIT 1")
    suspend fun findByNomAndPin(nom: String, codePin: String): Utilisateur?

    @Query("SELECT * FROM utilisateurs WHERE role = :role AND actif = 1")
    fun getEmployesActifs(role: Role = Role.EMPLOYE): Flow<List<Utilisateur>>

    @Query("SELECT * FROM utilisateurs WHERE actif = 1")
    fun getAllActifs(): Flow<List<Utilisateur>>

    @Query("SELECT * FROM utilisateurs WHERE actif = 1 AND role = 'EMPLOYE'")
    suspend fun getEmployesActifsSync(): List<Utilisateur>

    @Query("SELECT COUNT(*) FROM utilisateurs WHERE role = 'PATRONNE'")
    suspend fun countPatronne(): Int

    @Query("SELECT * FROM utilisateurs WHERE role = 'EMPLOYE' ORDER BY nom")
    fun getAllEmployes(): Flow<List<Utilisateur>>
}