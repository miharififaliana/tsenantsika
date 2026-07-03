package com.example.tsenantsika.ui.common

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarBus {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val messages = _messages.asSharedFlow()
    fun show(message: String) { _messages.tryEmit(message) }
}

@Composable
fun rememberBoutiqueSnackbarHostState(): SnackbarHostState {
    val state = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        SnackbarBus.messages.collect { state.showSnackbar(it) }
    }
    return state
}

@Composable
fun BoutiqueSnackbarHost(state: SnackbarHostState) = SnackbarHost(hostState = state)
