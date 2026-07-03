package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.*
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import com.example.tsenantsika.network.models.WsEventType
import java.time.Instant

data class LignePanier(
    val categorieId: Long,
    val prixReference: Long,
    val quantite: Int,
    val sousTotal: Long
)

class VenteRepository(
    database: BoutiqueDatabase,
    private val parametreCommissionRepository: ParametreCommissionRepository,
    private val auditRepository: AuditRepository,
    private val eventBus: EventBus
) : BaseRepository(database) {

    private val venteDao = database.venteDao()
    private val ligneDao = database.ligneVenteDao()
    private val journeeDao = database.journeeDao()

    suspend fun createVente(
        vendeurId: Long,
        journeeId: Long,
        lignes: List<LignePanier>,
        prixFinal: Long,
        utilisateurId: Long
    ): Long = withTransaction {
        val journee = journeeDao.getById(journeeId)
            ?: throw IllegalStateException("Journée introuvable")
        if (journee.statut != StatutJournee.OUVERTE) throw IllegalStateException("Journée clôturée")

        val commissionUnitaire = parametreCommissionRepository.getDernierParametre()?.montantCommissionParPiece
            ?: 1000L
        val totalPieces = lignes.sumOf { it.quantite }
        val commissionTotale = totalPieces * commissionUnitaire

        val vente = Vente(
            prixTotalFacture = prixFinal,
            nombrePiecesTotal = totalPieces,
            commissionUnitaireUtilisee = commissionUnitaire,
            commissionTotale = commissionTotale,
            vendeurId = vendeurId,
            journeeId = journeeId
        )
        val venteId = venteDao.insert(vente)
        val lignesEntities = lignes.map {
            LigneVente(
                venteId = venteId,
                categorieId = it.categorieId,
                prixReferenceHistorique = it.prixReference,
                quantite = it.quantite,
                sousTotal = it.sousTotal
            )
        }
        ligneDao.insertAll(lignesEntities)
        auditRepository.log(ActionAudit.AJOUT_VENTE, utilisateurId, "Vente #$venteId créée", venteId)
        eventBus.broadcast(WsEvent(WsEventType.VENTE_CREEE, venteId.toString()))
        venteId
    }

    suspend fun updateVente(
        venteId: Long,
        vendeurId: Long,
        lignes: List<LignePanier>,
        prixFinal: Long,
        utilisateurId: Long
    ) = withTransaction {
        val existing = venteDao.getById(venteId) ?: throw IllegalStateException("Vente introuvable")
        val journee = journeeDao.getById(existing.journeeId)
            ?: throw IllegalStateException("Journée introuvable")
        if (journee.statut != StatutJournee.OUVERTE) throw IllegalStateException("Journée clôturée")

        val commissionUnitaire = existing.commissionUnitaireUtilisee
        val totalPieces = lignes.sumOf { it.quantite }
        val updated = existing.copy(
            vendeurId = vendeurId,
            prixTotalFacture = prixFinal,
            nombrePiecesTotal = totalPieces,
            commissionTotale = totalPieces * commissionUnitaire,
            statut = StatutVente.MODIFIEE
        )
        venteDao.update(updated)
        ligneDao.deleteLignesByVenteId(venteId)
        ligneDao.insertAll(lignes.map {
            LigneVente(venteId = venteId, categorieId = it.categorieId,
                prixReferenceHistorique = it.prixReference, quantite = it.quantite, sousTotal = it.sousTotal)
        })
        auditRepository.log(ActionAudit.MODIFICATION_VENTE, utilisateurId, "Vente #$venteId modifiée", venteId)
        eventBus.broadcast(WsEvent(WsEventType.VENTE_MODIFIEE, venteId.toString()))
    }

    suspend fun deleteVente(venteId: Long, motif: String, utilisateurId: Long) = withTransaction {
        require(motif.isNotBlank()) { "Motif obligatoire" }
        val existing = venteDao.getById(venteId) ?: throw IllegalStateException("Vente introuvable")
        val journee = journeeDao.getById(existing.journeeId)
            ?: throw IllegalStateException("Journée introuvable")
        if (journee.statut != StatutJournee.OUVERTE) throw IllegalStateException("Journée clôturée")

        ligneDao.deleteLignesByVenteId(venteId)
        venteDao.delete(existing)
        auditRepository.log(ActionAudit.SUPPRESSION_VENTE, utilisateurId, motif, venteId)
        eventBus.broadcast(WsEvent(WsEventType.VENTE_SUPPRIMEE, venteId.toString()))
    }

    suspend fun getLignes(venteId: Long) = safeCall { ligneDao.getLignesByVente(venteId) }

    fun getVentesByJournee(journeeId: Long) = venteDao.getVentesByJournee(journeeId)

    fun getVentesByVendeur(journeeId: Long, vendeurId: Long) =
        venteDao.getVentesByVendeur(journeeId, vendeurId)

    suspend fun getChiffreAffairesJournee(journeeId: Long) =
        safeCall { venteDao.getChiffreAffairesJournee(journeeId) }

    suspend fun getCommissionBruteVendeur(journeeId: Long, vendeurId: Long) =
        safeCall { venteDao.getCommissionBruteVendeur(journeeId, vendeurId) }

    suspend fun getTotalPiecesVendeur(journeeId: Long, vendeurId: Long) =
        safeCall { venteDao.getTotalPiecesVendeur(journeeId, vendeurId) ?: 0 }

    suspend fun getTotalPiecesAutres(journeeId: Long, vendeurId: Long) =
        safeCall { venteDao.getTotalPiecesAutresVendeurs(journeeId, vendeurId) ?: 0 }

    suspend fun getTotalPiecesJournee(journeeId: Long) =
        safeCall { venteDao.getTotalPiecesJournee(journeeId) ?: 0 }

    suspend fun getVenteById(id: Long) = safeCall { venteDao.getById(id) }
}
