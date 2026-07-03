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

    suspend fun getById(id: Long): Utilisateur? = safeCall { dao.getById(id) }

    fun getAllEmployes(): Flow<List<Utilisateur>> = dao.getAllEmployes()

    suspend fun createEmploye(nom: String, pin: String): Long = safeCall {
        dao.insert(Utilisateur(nom = nom.trim(), codePin = pin, role = Role.EMPLOYE))
    }

    suspend fun deactivateEmploye(id: Long) = safeCall {
        dao.getById(id)?.let { dao.update(it.copy(actif = false)) }
    }

    suspend fun resetPin(id: Long, newPin: String) = safeCall {
        require(newPin.length == 4 && newPin.all { it.isDigit() })
        dao.getById(id)?.let { dao.update(it.copy(codePin = newPin)) }
    }
}