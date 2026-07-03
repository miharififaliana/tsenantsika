package com.example.tsenantsika.ui.patronne

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsenantsika.data.entities.Journee
import com.example.tsenantsika.data.entities.StatutJournee
import com.example.tsenantsika.data.repositories.JourneeRepository
import com.example.tsenantsika.ui.common.SnackbarBus
import com.example.tsenantsika.data.repositories.JourneeStats
import com.example.tsenantsika.data.repositories.StatsRepository
import com.example.tsenantsika.ui.common.*
import com.example.tsenantsika.ui.theme.AlertRed
import com.example.tsenantsika.ui.theme.SecondaryBlue
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.Instant

class JourneeViewModel(
    private val journeeRepo: JourneeRepository,
    private val statsRepo: StatsRepository
) : ViewModel() {
    private val _journee = MutableStateFlow<Journee?>(null)
    val journee: StateFlow<Journee?> = _journee.asStateFlow()
    private val _stats = MutableStateFlow(JourneeStats())
    val stats: StateFlow<JourneeStats> = _stats.asStateFlow()
    val historique = journeeRepo.getHistorique().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    var error by mutableStateOf<String?>(null); private set
    var showClotureDialog by mutableStateOf(false); private set

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val j = journeeRepo.getJourneeOuverte()
            _journee.value = j
            if (j != null) {
                launch {
                    statsRepo.observeStatsJournee(j.idJournee).collect { _stats.value = it }
                }
            }
        }
    }

    fun ouvrir() {
        viewModelScope.launch {
            runCatching { journeeRepo.ouvrirJournee() }.onSuccess {
                refresh()
                SnackbarBus.show("Journée ouverte")
            }.onFailure { error = it.message }
        }
    }

    fun cloturer() {
        viewModelScope.launch {
            val j = _journee.value ?: return@launch
            runCatching { journeeRepo.cloturerJournee(j.idJournee) }.onSuccess {
                showClotureDialog = false; refresh()
                SnackbarBus.show("Journée clôturée")
            }.onFailure { error = it.message }
        }
    }

    fun requestCloture() { showClotureDialog = true }
    fun dismissCloture() { showClotureDialog = false }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatronneHomeScreen(
    onNavigate: (String) -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    vm: JourneeViewModel = koinViewModel()
) {
    val journee by vm.journee.collectAsState()
    val stats by vm.stats.collectAsState()

    Scaffold(
        topBar = {
            BoutiqueTopBar("Tableau de bord", actions = {
                ConnectivityIndicator()
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, null)
                }
            })
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (journee == null) {
                PrimaryButton("Ouvrir une nouvelle journée", onClick = { vm.ouvrir() })
            } else {
                val duree = Duration.between(journee!!.heureOuverture, Instant.now())
                Text("Journée ouverte depuis ${duree.toHours()}h ${duree.toMinutesPart()}min")
                StatCard("Chiffre d'affaires", formatAr(stats.chiffreAffaires) + " Ar")
                StatCard("Dépenses", formatAr(stats.totalDepenses) + " Ar", AlertRed)
                StatCard("Avances", formatAr(stats.totalAvances) + " Ar", AlertRed)
                StatCard("Bénéfice net", formatAr(stats.beneficeNet) + " Ar")
                StatCard("Ventes", "${stats.nombreVentes}")
                StatCard("Pièces vendues", "${stats.totalPieces}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SecondaryButton("Clôturer", onClick = { vm.requestCloture() }, modifier = Modifier.weight(1f))
                    PrimaryButton("Nouvelle vente", onClick = { onNavigate(com.example.tsenantsika.ui.navigation.Routes.VENTE_CREATE) }, modifier = Modifier.weight(1f))
                }
            }
            NavigationGrid(onNavigate)
            TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Déconnexion", color = AlertRed) }
        }
    }
    if (vm.showClotureDialog) {
        AlertDialog(
            onDismissRequest = { vm.dismissCloture() },
            title = { Text("Clôturer la journée ?") },
            text = { Text("Les données seront archivées et verrouillées.") },
            confirmButton = { TextButton(onClick = { vm.cloturer() }) { Text("Confirmer") } },
            dismissButton = { TextButton(onClick = { vm.dismissCloture() }) { Text("Annuler") } }
        )
    }
}

@Composable
private fun NavigationGrid(onNavigate: (String) -> Unit) {
    val routes = listOf(
        "Ventes" to com.example.tsenantsika.ui.navigation.Routes.VENTE_LIST,
        "Avances" to com.example.tsenantsika.ui.navigation.Routes.AVANCES,
        "Dépenses" to com.example.tsenantsika.ui.navigation.Routes.DEPENSES,
        "Employés" to com.example.tsenantsika.ui.navigation.Routes.EMPLOYE_MANAGEMENT,
        "Historique" to com.example.tsenantsika.ui.navigation.Routes.JOURNEE_HISTORIQUE,
        "Audit" to com.example.tsenantsika.ui.navigation.Routes.AUDIT,
        "Admin" to com.example.tsenantsika.ui.navigation.Routes.ADMIN_SETTINGS
    )
    routes.chunked(2).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            row.forEach { (label, route) ->
                OutlinedButton(onClick = { onNavigate(route) }, modifier = Modifier.weight(1f)) { Text(label) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneeHistoriqueScreen(onBack: () -> Unit, vm: JourneeViewModel = koinViewModel()) {
    val historique by vm.historique.collectAsState()
    Scaffold(topBar = { BoutiqueTopBar("Historique", actions = { TextButton(onClick = onBack) { Text("Retour") } }) }) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(historique) { j ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("#${j.idJournee} - ${j.statut}", style = MaterialTheme.typography.titleMedium)
                        Text("CA: ${formatAr(j.chiffreAffaires)} Ar")
                        Text("Bénéfice: ${formatAr(j.beneficeNet)} Ar")
                    }
                }
            }
        }
    }
}
