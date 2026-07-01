package com.example.tsenantsika.data.repositories

import androidx.room.withTransaction
import com.example.tsenantsika.data.database.BoutiqueDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Base abstraite pour tous les repositories.
 * Centralise la gestion des threads (IO) et des exceptions.
 */
abstract class BaseRepository(
    protected val database: BoutiqueDatabase,
    protected val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Exécute une opération en contexte IO avec gestion d'erreur centralisée.
     */
    protected suspend fun <T> safeCall(block: suspend () -> T): T {
        return try {
            withContext(dispatcher) {
                block()
            }
        } catch (e: Exception) {
            Timber.e(e, "Erreur dans repository ${this::class.java.simpleName}")
            throw e // Propagation pour gestion dans ViewModel
        }
    }

    /**
     * Exécute une transaction Room avec sécurité.
     */
    protected suspend fun <T> withTransaction(block: suspend () -> T): T {
        return safeCall {
            database.withTransaction(block)
        }
    }
}