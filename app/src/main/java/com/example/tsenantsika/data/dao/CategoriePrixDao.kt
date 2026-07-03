package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.CategoriePrix
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriePrixDao {

    @Insert
    suspend fun insert(categorie: CategoriePrix): Long

    @Update
    suspend fun update(categorie: CategoriePrix)

    @Query("SELECT * FROM categories_prix WHERE actif = 1")
    fun getCategoriesActives(): Flow<List<CategoriePrix>>

    @Query("SELECT * FROM categories_prix")
    fun getAll(): Flow<List<CategoriePrix>>

    @Query("SELECT * FROM categories_prix")
    suspend fun getAllSync(): List<CategoriePrix>

    @Query("SELECT * FROM categories_prix WHERE idCategorie = :id")
    suspend fun getById(id: Long): CategoriePrix?
}