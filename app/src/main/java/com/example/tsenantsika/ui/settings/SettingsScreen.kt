package com.example.tsenantsika.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tsenantsika.data.entities.Role
import com.example.tsenantsika.ui.auth.EditPinScreen
import com.example.tsenantsika.ui.common.BoutiqueTopBar
import com.example.tsenantsika.ui.navigation.Routes
import com.example.tsenantsika.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userId: Long,
    role: Role,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(topBar = {
        BoutiqueTopBar("Paramètres", actions = { TextButton(onClick = onBack) { Text("Retour") } })
    }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (role == Role.PATRONNE) {
                OutlinedButton(onClick = { onNavigate(Routes.ADMIN_SETTINGS) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Administration (catégories, commission)", color = SecondaryBlue)
                }
                OutlinedButton(onClick = { onNavigate(Routes.EMPLOYE_MANAGEMENT) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Gestion des employés", color = SecondaryBlue)
                }
                OutlinedButton(onClick = { onNavigate(Routes.AUDIT) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Journal d'audit", color = SecondaryBlue)
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }
            Text("Modifier mon PIN", style = MaterialTheme.typography.titleMedium)
            EditPinScreen(userId = userId, onDone = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
