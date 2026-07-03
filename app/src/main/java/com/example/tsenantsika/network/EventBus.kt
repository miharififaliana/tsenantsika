package com.example.tsenantsika.network

import com.example.tsenantsika.network.models.WsEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArrayList

class EventBus {
    private val json = Json { encodeDefaults = true }
    private val _events = MutableSharedFlow<WsEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<WsEvent> = _events.asSharedFlow()

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val wsSessions = CopyOnWriteArrayList<(String) -> Unit>()

    fun setConnected(value: Boolean) { _connected.value = value }

    fun registerSession(send: (String) -> Unit) { wsSessions.add(send) }
    fun unregisterSession(send: (String) -> Unit) { wsSessions.remove(send) }

    fun broadcast(event: WsEvent) {
        val msg = json.encodeToString(event)
        wsSessions.forEach { runCatching { it(msg) } }
        _events.tryEmit(event)
    }

    fun dispatch(event: WsEvent) {
        _events.tryEmit(event)
    }
}
