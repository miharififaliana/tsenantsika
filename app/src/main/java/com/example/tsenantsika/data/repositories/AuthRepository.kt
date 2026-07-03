package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.entities.Role
import com.example.tsenantsika.data.entities.Utilisateur
import com.example.tsenantsika.utils.SessionManager
import java.time.Instant

sealed class AuthResult {
    data class Success(val user: Utilisateur) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val utilisateurRepository: UtilisateurRepository,
    private val sessionManager: SessionManager
) {
    suspend fun login(nom: String, pin: String): AuthResult {
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            return AuthResult.Error("Le PIN doit contenir 4 chiffres")
        }
        val user = utilisateurRepository.findByNomAndPin(nom.trim(), pin)
            ?: return AuthResult.Error("Nom ou PIN incorrect")
        if (!user.actif) return AuthResult.Error("Compte désactivé")
        sessionManager.saveSession(user.idUtilisateur, user.nom, user.role)
        return AuthResult.Success(user)
    }

    suspend fun createPatronne(nom: String, pin: String): AuthResult {
        if (utilisateurRepository.countPatronne() > 0) {
            return AuthResult.Error("Un compte Patronne existe déjà")
        }
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            return AuthResult.Error("Le PIN doit contenir 4 chiffres")
        }
        val id = utilisateurRepository.insert(
            Utilisateur(nom = nom.trim(), codePin = pin, role = Role.PATRONNE)
        )
        val user = Utilisateur(idUtilisateur = id, nom = nom.trim(), codePin = pin, role = Role.PATRONNE)
        sessionManager.saveSession(id, user.nom, user.role)
        return AuthResult.Success(user)
    }

    suspend fun changePin(userId: Long, oldPin: String, newPin: String): AuthResult {
        if (newPin.length != 4 || !newPin.all { it.isDigit() }) {
            return AuthResult.Error("Le nouveau PIN doit contenir 4 chiffres")
        }
        val user = utilisateurRepository.getById(userId)
            ?: return AuthResult.Error("Utilisateur introuvable")
        if (user.codePin != oldPin) return AuthResult.Error("Ancien PIN incorrect")
        utilisateurRepository.update(user.copy(codePin = newPin))
        return AuthResult.Success(user.copy(codePin = newPin))
    }

    suspend fun logout() = sessionManager.clearSession()
}
