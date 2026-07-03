package com.example.tsenantsika.network.client

import com.example.tsenantsika.data.entities.CategoriePrix
import com.example.tsenantsika.data.entities.Journee
import com.example.tsenantsika.data.entities.ParametreCommission
import com.example.tsenantsika.data.entities.Utilisateur
import com.example.tsenantsika.data.entities.Vente
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.models.WsEvent
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import timber.log.Timber

class NetworkClient(
    private val eventBus: EventBus,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private var serverIp: String? = null
    private var wsJob: Job? = null
    private var reconnectAttempt = 0

    private val json = Json { ignoreUnknownKeys = true }

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { json(json) }
        install(WebSockets)
    }

    fun connect(ip: String) {
        serverIp = ip
        connectWebSocket()
    }

    fun disconnect() {
        wsJob?.cancel()
        eventBus.setConnected(false)
    }

    suspend fun fetchJournee(): Journee? = get("/api/journee/actuelle")
    suspend fun fetchEmployes(): List<Utilisateur> = get("/api/employes") ?: emptyList()
    suspend fun fetchCategories(): List<CategoriePrix> = get("/api/categories") ?: emptyList()
    suspend fun fetchCommission(): ParametreCommission? = get("/api/commission")
    suspend fun fetchVentes(journeeId: Long): List<Vente> = get("/api/ventes/$journeeId") ?: emptyList()

    private suspend inline fun <reified T> get(path: String): T? = runCatching {
        val ip = serverIp ?: return null
        httpClient.get("http://$ip:8080$path").body<T>()
    }.onFailure { Timber.w("GET $path failed: ${it.message}") }.getOrNull()

    private fun connectWebSocket() {
        wsJob?.cancel()
        wsJob = scope.launch {
            val ip = serverIp ?: return@launch
            while (isActive) {
                try {
                    httpClient.webSocket("ws://$ip:8080/ws") {
                        eventBus.setConnected(true)
                        reconnectAttempt = 0
                        Timber.i("Client connecté au WebSocket")
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                runCatching {
                                    json.decodeFromString(WsEvent.serializer(), frame.readText())
                                }.onSuccess { eventBus.dispatch(it) }
                            }
                        }
                    }
                } catch (e: Exception) {
                    eventBus.setConnected(false)
                    val delayMs = minOf(30_000L, 1000L * (1 shl reconnectAttempt))
                    reconnectAttempt++
                    delay(delayMs)
                }
            }
        }
    }
}
