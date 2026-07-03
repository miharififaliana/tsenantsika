package com.example.tsenantsika.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsenantsika.data.entities.Utilisateur
import com.example.tsenantsika.data.repositories.UtilisateurRepository
import com.example.tsenantsika.ui.common.BoutiqueTopBar
import com.example.tsenantsika.ui.common.PrimaryButton
import com.example.tsenantsika.ui.theme.AlertRed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class EmployeViewModel(private val repo: UtilisateurRepository) : ViewModel() {
    val employes = repo.getAllEmployes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    var error by mutableStateOf<String?>(null); private set

    fun add(nom: String, pin: String) {
        if (pin.length != 4) { error = "PIN 4 chiffres requis"; return }
        viewModelScope.launch { repo.createEmploye(nom, pin); error = null }
    }
    fun deactivate(id: Long) { viewModelScope.launch { repo.deactivateEmploye(id) } }
    fun resetPin(id: Long, pin: String) {
        if (pin.length != 4) { error = "PIN 4 chiffres requis"; return }
        viewModelScope.launch { repo.resetPin(id, pin); error = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeManagementScreen(onBack: () -> Unit, vm: EmployeViewModel = koinViewModel()) {
    val employes by vm.employes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BoutiqueTopBar("Employés", actions = { TextButton(onClick = onBack) { Text("Retour") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(employes) { e ->
                EmployeCard(
                    employe = e,
                    onDeactivate = { vm.deactivate(e.idUtilisateur) },
                    onResetPin = { pin -> vm.resetPin(e.idUtilisateur, pin) }
                )
            }
        }
    }
    if (showDialog) AddEmployeDialog(onDismiss = { showDialog = false }, onAdd = { n, p -> vm.add(n, p); showDialog = false })
}

@Composable
private fun EmployeCard(employe: Utilisateur, onDeactivate: () -> Unit, onResetPin: (String) -> Unit) {
    var showReset by remember { mutableStateOf(false) }
    var newPin by remember { mutableStateOf("") }
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(employe.nom, style = MaterialTheme.typography.titleMedium)
                Text(if (employe.actif) "Actif" else "Inactif", color = if (employe.actif) MaterialTheme.colorScheme.primary else AlertRed)
            }
            Row {
                if (employe.actif) {
                    TextButton(onClick = { showReset = true }) { Text("PIN") }
                    TextButton(onClick = onDeactivate) { Text("Désactiver", color = AlertRed) }
                }
            }
        }
    }
    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("Réinitialiser PIN — ${employe.nom}") },
            text = {
                OutlinedTextField(value = newPin, onValueChange = { if (it.length <= 4) newPin = it },
                    label = { Text("Nouveau PIN") }, visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
            },
            confirmButton = {
                TextButton(onClick = { onResetPin(newPin); showReset = false; newPin = "" }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showReset = false }) { Text("Annuler") } }
        )
    }
}

@Composable
private fun AddEmployeDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var nom by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvel employé") },
        text = {
            Column {
                OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom") })
                OutlinedTextField(value = pin, onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text("PIN") }, visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
            }
        },
        confirmButton = { TextButton(onClick = { onAdd(nom, pin) }) { Text("Ajouter") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
