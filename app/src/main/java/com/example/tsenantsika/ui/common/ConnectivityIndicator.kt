package com.example.tsenantsika.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.ui.theme.AlertRed
import com.example.tsenantsika.ui.theme.PrimaryGreen
import org.koin.compose.koinInject

@Composable
fun ConnectivityIndicator(eventBus: EventBus = koinInject()) {
    val connected by eventBus.connected.collectAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            Modifier.size(8.dp).clip(CircleShape)
                .background(if (connected) PrimaryGreen else AlertRed)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            if (connected) "Connecté" else "Hors ligne",
            style = MaterialTheme.typography.labelSmall
        )
    }
}
