package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

import kotlinx.serialization.Serializable

@Serializable
data class JourneeStats(
    val chiffreAffaires: Long = 0,
    val totalDepenses: Long = 0,
    val totalAvances: Long = 0,
    val totalCommissions: Long = 0,
    val nombreVentes: Int = 0,
    val totalPieces: Int = 0,
    val beneficeNet: Long = 0
)

class StatsRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val venteDao = database.venteDao()
    private val depenseDao = database.depenseBoutiqueDao()
    private val avanceDao = database.avanceEmployeDao()

    suspend fun getStatsJournee(journeeId: Long): JourneeStats = safeCall {
        val ca = venteDao.getChiffreAffairesJournee(journeeId) ?: 0L
        val depenses = depenseDao.getTotalDepenses(journeeId) ?: 0L
        val avances = avanceDao.getTotalAvancesJournee(journeeId) ?: 0L
        val commissions = venteDao.getTotalCommissionsJournee(journeeId) ?: 0L
        val nbVentes = venteDao.countVentesJournee(journeeId)
        val pieces = venteDao.getTotalPiecesJournee(journeeId) ?: 0
        JourneeStats(ca, depenses, avances, commissions, nbVentes, pieces, ca - depenses)
    }

    fun observeStatsJournee(journeeId: Long): Flow<JourneeStats> = combine(
        combine(
            venteDao.observeChiffreAffaires(journeeId),
            depenseDao.observeTotalDepenses(journeeId),
            avanceDao.observeTotalAvances(journeeId)
        ) { ca, dep, av -> Triple(ca, dep, av) },
        combine(
            venteDao.observeTotalCommissions(journeeId),
            venteDao.observeCountVentes(journeeId),
            venteDao.observeTotalPieces(journeeId)
        ) { comm, nb, pieces -> Triple(comm, nb, pieces) }
    ) { (ca, dep, av), (comm, nb, pieces) ->
        JourneeStats(ca ?: 0, dep ?: 0, av ?: 0, comm ?: 0, nb, pieces ?: 0, (ca ?: 0) - (dep ?: 0))
    }
}
