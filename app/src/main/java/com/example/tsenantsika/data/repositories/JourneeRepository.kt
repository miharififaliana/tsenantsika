package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.Journee
import com.example.tsenantsika.data.entities.StatutJournee
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import com.example.tsenantsika.network.models.WsEventType
import java.time.Instant
import kotlinx.coroutines.flow.Flow

class JourneeRepository(
    database: BoutiqueDatabase,
    private val statsRepository: StatsRepository,
    private val eventBus: EventBus
) : BaseRepository(database) {

    private val dao = database.journeeDao()

    suspend fun ouvrirJournee(): Long = withTransaction {
        if (dao.getJourneeOuverte() != null) throw IllegalStateException("Une journée est déjà ouverte")
        dao.insert(Journee(heureOuverture = Instant.now(), statut = StatutJournee.OUVERTE))
    }.also { eventBus.broadcast(WsEvent(WsEventType.JOURNEE_OUVERTE)) }

    suspend fun cloturerJournee(journeeId: Long) = withTransaction {
        val journee = dao.getById(journeeId) ?: throw IllegalStateException("Journée introuvable")
        if (journee.statut != StatutJournee.OUVERTE) throw IllegalStateException("Journée déjà clôturée")
        val stats = statsRepository.getStatsJournee(journeeId)
        dao.update(journee.copy(
            statut = StatutJournee.CLOTUREE,
            heureCloture = Instant.now(),
            chiffreAffaires = stats.chiffreAffaires,
            depensesBoutique = stats.totalDepenses,
            beneficeNet = stats.beneficeNet
        ))
    }.also { eventBus.broadcast(WsEvent(WsEventType.JOURNEE_CLOTUREE)) }

    suspend fun insert(journee: Journee): Long = safeCall { dao.insert(journee) }
    suspend fun update(journee: Journee) = safeCall { dao.update(journee) }
    suspend fun getJourneeOuverte(): Journee? = safeCall { dao.getJourneeOuverte() }
    suspend fun getById(id: Long): Journee? = safeCall { dao.getById(id) }
    fun getHistorique(): Flow<List<Journee>> = dao.getHistorique()
}
