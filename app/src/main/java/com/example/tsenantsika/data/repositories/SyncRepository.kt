package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.*
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.client.NetworkClient
import com.example.tsenantsika.network.models.WsEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class SyncRepository(
    private val database: BoutiqueDatabase,
    private val networkClient: NetworkClient,
    private val eventBus: EventBus,
    private val utilisateurRepo: UtilisateurRepository,
    private val categorieRepo: CategoriePrixRepository,
    private val commissionRepo: ParametreCommissionRepository,
    private val journeeRepo: JourneeRepository,
    private val venteRepo: VenteRepository,
    private val avanceRepo: AvanceEmployeRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startListening() {
        eventBus.events.onEach { event ->
            Timber.d("Sync event: ${event.type}")
            when (event.type) {
                WsEventType.VENTE_CREEE, WsEventType.VENTE_MODIFIEE, WsEventType.VENTE_SUPPRIMEE,
                WsEventType.AVANCE_AJOUTEE, WsEventType.DEPENSE_AJOUTEE,
                WsEventType.JOURNEE_OUVERTE, WsEventType.JOURNEE_CLOTUREE,
                WsEventType.PARAMETRE_COMMISSION_MODIFIE, WsEventType.CATEGORIE_MODIFIEE ->
                    scope.launch { syncFromServer() }
                else -> Unit
            }
        }.launchIn(scope)
    }

    suspend fun syncFromServer() {
        val journee = networkClient.fetchJournee() ?: return
        val existing = journeeRepo.getJourneeOuverte()
        if (existing == null) journeeRepo.insert(journee)
        else if (existing.idJournee != journee.idJournee) journeeRepo.update(journee)

        networkClient.fetchEmployes().forEach { e ->
            utilisateurRepo.insert(e)
        }
        networkClient.fetchCategories().forEach { c ->
            categorieRepo.insert(c)
        }
        networkClient.fetchCommission()?.let { commissionRepo.insert(it) }

        val jId = journee.idJournee
        networkClient.fetchVentes(jId).forEach { v ->
            database.venteDao().insert(v)
        }
    }

    suspend fun initialSync() = syncFromServer()
}
