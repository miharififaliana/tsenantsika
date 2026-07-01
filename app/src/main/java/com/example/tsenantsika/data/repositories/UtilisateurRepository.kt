package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.Role
import com.example.tsenantsika.data.entities.Utilisateur
import kotlinx.coroutines.flow.Flow

class UtilisateurRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.utilisateurDao()

    suspend fun insert(utilisateur: Utilisateur): Long = safeCall { dao.insert(utilisateur) }

    suspend fun update(utilisateur: Utilisateur) = safeCall { dao.update(utilisateur) }

    fun getEmployesActifs(): Flow<List<Utilisateur>> = dao.getEmployesActifs()

    suspend fun findByNomAndPin(nom: String, codePin: String): Utilisateur? = safeCall {
        dao.findByNomAndPin(nom, codePin)
    }

    suspend fun countPatronne(): Int = safeCall { dao.countPatronne() }
}