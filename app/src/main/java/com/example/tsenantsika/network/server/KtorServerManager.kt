package com.example.tsenantsika.network.server

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.AvanceEmploye
import com.example.tsenantsika.data.entities.DepenseBoutique
import com.example.tsenantsika.data.repositories.*
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import com.example.tsenantsika.network.models.WsEventType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.Duration

@Serializable data class VenteRequest(
    val vendeurId: Long, val journeeId: Long, val prixFinal: Long,
    val lignes: List<LigneRequest>, val utilisateurId: Long
)
@Serializable data class LigneRequest(val categorieId: Long, val prixReference: Long, val quantite: Int, val sousTotal: Long)
@Serializable data class AvanceRequest(val employeId: Long, val journeeId: Long, val montant: Long, val motif: String? = null)
@Serializable data class DepenseRequest(val journeeId: Long, val libelle: String, val montant: Long)
@Serializable data class DeleteVenteRequest(val motif: String, val utilisateurId: Long)

class KtorServerManager(
    private val database: BoutiqueDatabase,
    private val journeeRepository: JourneeRepository,
    private val venteRepository: VenteRepository,
    private val avanceRepository: AvanceEmployeRepository,
    private val depenseRepository: DepenseBoutiqueRepository,
    private val statsRepository: StatsRepository,
    private val commissionRepository: ParametreCommissionRepository,
    private val eventBus: EventBus
) {
    private var server: ApplicationEngine? = null
    val port = 8080

    fun start() {
        if (server != null) return
        server = embeddedServer(CIO, port = port) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; isLenient = true }) }
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(30)
            }
            install(CORS) { anyHost() }
            routing {
                get("/api/journee/actuelle") {
                    val j = journeeRepository.getJourneeOuverte()
                    if (j == null) call.respond(HttpStatusCode.NoContent) else call.respond(j)
                }
                get("/api/employes") { call.respond(database.utilisateurDao().getEmployesActifsSync()) }
                get("/api/categories") { call.respond(database.categoriePrixDao().getAllSync()) }
                get("/api/commission") {
                    val p = commissionRepository.getDernierParametre()
                    if (p == null) call.respond(HttpStatusCode.NoContent) else call.respond(p)
                }
                get("/api/stats/journalieres") {
                    val j = journeeRepository.getJourneeOuverte()
                    if (j == null) call.respond(mapOf("error" to "no_journee"))
                    else call.respond(statsRepository.getStatsJournee(j.idJournee))
                }
                get("/api/ventes/{journeeId}") {
                    val id = call.parameters["journeeId"]?.toLongOrNull()
                        ?: return@get call.respond(mapOf("error" to "invalid"))
                    call.respond(database.venteDao().getVentesSync(id))
                }
                post("/api/vente") {
                    val req = call.receive<VenteRequest>()
                    val id = venteRepository.createVente(
                        req.vendeurId, req.journeeId,
                        req.lignes.map { LignePanier(it.categorieId, it.prixReference, it.quantite, it.sousTotal) },
                        req.prixFinal, req.utilisateurId
                    )
                    call.respond(mapOf("id" to id))
                }
                delete("/api/vente/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(mapOf("error" to "invalid"))
                    val req = call.receive<DeleteVenteRequest>()
                    venteRepository.deleteVente(id, req.motif, req.utilisateurId)
                    call.respond(mapOf("ok" to true))
                }
                post("/api/avance") {
                    val req = call.receive<AvanceRequest>()
                    val id = avanceRepository.addAvance(
                        AvanceEmploye(employeId = req.employeId, montant = req.montant, journeeId = req.journeeId, motif = req.motif), 0L
                    )
                    call.respond(mapOf("id" to id))
                }
                post("/api/depense") {
                    val req = call.receive<DepenseRequest>()
                    val id = depenseRepository.addDepense(
                        DepenseBoutique(libelle = req.libelle, montant = req.montant, journeeId = req.journeeId), 0L
                    )
                    call.respond(mapOf("id" to id))
                }
                webSocket("/ws") {
                    val sender: (String) -> Unit = { msg ->
                        kotlinx.coroutines.runBlocking { send(Frame.Text(msg)) }
                    }
                    eventBus.registerSession(sender)
                    try {
                        for (frame in incoming) {
                            if (frame is Frame.Text && frame.readText() == "ping") {
                                send(Frame.Text(Json.encodeToString(WsEvent.serializer(), WsEvent(WsEventType.PING))))
                            }
                        }
                    } finally {
                        eventBus.unregisterSession(sender)
                    }
                }
            }
        }.start(wait = false)
        Timber.i("Serveur Ktor démarré sur le port $port")
    }

    fun stop() {
        server?.stop(1000, 2000)
        server = null
    }

    fun isRunning() = server != null
}
