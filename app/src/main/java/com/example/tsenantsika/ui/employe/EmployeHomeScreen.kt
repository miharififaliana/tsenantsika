package com.example.tsenantsika.ui.employe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsenantsika.data.entities.Vente
import com.example.tsenantsika.data.repositories.*
import com.example.tsenantsika.ui.common.*
import com.example.tsenantsika.ui.theme.AlertRed
import com.example.tsenantsika.ui.theme.PrimaryGreen
import com.example.tsenantsika.ui.theme.SecondaryBlue
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

data class EmployeResume(
    val mesPieces: Int = 0,
    val piecesCollegues: Int = 0,
    val totalPieces: Int = 0,
    val commissionBrute: Long = 0,
    val totalAvances: Long = 0,
    val resteAPayer: Long = 0
)

class EmployeViewModel(
    private val venteRepo: VenteRepository,
    private val avanceRepo: AvanceEmployeRepository,
    private val journeeRepo: JourneeRepository
) : ViewModel() {
    private val _resume = MutableStateFlow(EmployeResume())
    val resume: StateFlow<EmployeResume> = _resume.asStateFlow()
    private val _ventes = MutableStateFlow<List<Vente>>(emptyList())
    val ventes: StateFlow<List<Vente>> = _ventes.asStateFlow()

    fun load(userId: Long) {
        viewModelScope.launch {
            val journee = journeeRepo.getJourneeOuverte() ?: return@launch
            val jId = journee.idJournee
            venteRepo.getVentesByVendeur(jId, userId).collect { _ventes.value = it }
            val mesPieces = venteRepo.getTotalPiecesVendeur(jId, userId)
            val autres = venteRepo.getTotalPiecesAutres(jId, userId)
            val total = venteRepo.getTotalPiecesJournee(jId)
            val comm = venteRepo.getCommissionBruteVendeur(jId, userId) ?: 0L
            val avances = avanceRepo.getTotalAvancesEmploye(jId, userId) ?: 0L
            _resume.value = EmployeResume(mesPieces, autres, total, comm, avances, comm - avances)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeHomeScreen(
    userId: Long,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    vm: EmployeViewModel = koinViewModel()
) {
    val resume by vm.resume.collectAsState()
    val ventes by vm.ventes.collectAsState()
    LaunchedEffect(userId) { vm.load(userId) }

    Scaffold(
        topBar = {
            BoutiqueTopBar("Mon résumé", actions = {
                ConnectivityIndicator()
                IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, null) }
            })
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Mes pièces vendues", "${resume.mesPieces}", PrimaryGreen)
            StatCard("Pièces collègues", "${resume.piecesCollegues}", SecondaryBlue)
            StatCard("Total boutique", "${resume.totalPieces}")
            StatCard("Commission brute", "${formatAr(resume.commissionBrute)} Ar", PrimaryGreen)
            StatCard("Avances reçues", "${formatAr(resume.totalAvances)} Ar", AlertRed)
            StatCard("Reste à payer", "${formatAr(resume.resteAPayer)} Ar", PrimaryGreen)
            Text("Mes ventes", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(ventes) { v ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${v.nombrePiecesTotal} pièces")
                            Text("${formatAr(v.commissionTotale)} Ar")
                        }
                    }
                }
            }
            TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Déconnexion", color = AlertRed) }
        }
    }
}
