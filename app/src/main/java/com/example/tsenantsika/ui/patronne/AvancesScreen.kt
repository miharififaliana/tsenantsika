package com.example.tsenantsika.ui.patronne

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsenantsika.data.entities.AvanceEmploye
import com.example.tsenantsika.data.entities.Utilisateur
import com.example.tsenantsika.data.repositories.AvanceEmployeRepository
import com.example.tsenantsika.data.repositories.JourneeRepository
import com.example.tsenantsika.data.repositories.UtilisateurRepository
import com.example.tsenantsika.ui.common.BoutiqueTopBar
import com.example.tsenantsika.ui.common.formatAr
import com.example.tsenantsika.ui.theme.AlertRed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class AvancesViewModel(
    private val avanceRepo: AvanceEmployeRepository,
    private val journeeRepo: JourneeRepository,
    private val utilisateurRepo: UtilisateurRepository
) : ViewModel() {
    var journeeId by mutableStateOf<Long?>(null); private set
    val employes = utilisateurRepo.getEmployesActifs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _avances = MutableStateFlow<List<AvanceEmploye>>(emptyList())
    val avances: StateFlow<List<AvanceEmploye>> = _avances.asStateFlow()

    init {
        viewModelScope.launch {
            journeeId = journeeRepo.getJourneeOuverte()?.idJournee
            journeeId?.let { id -> avanceRepo.getAvancesByJournee(id).collect { _avances.value = it } }
        }
    }

    fun add(employeId: Long, montant: Long, motif: String?) {
        val jId = journeeId ?: return
        viewModelScope.launch {
            avanceRepo.addAvance(AvanceEmploye(employeId = employeId, montant = montant, journeeId = jId, motif = motif), 0L)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvancesScreen(onBack: () -> Unit, vm: AvancesViewModel = koinViewModel()) {
    val avances by vm.avances.collectAsState()
    val employes by vm.employes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BoutiqueTopBar("Avances", actions = { TextButton(onClick = onBack) { Text("Retour") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(avances) { a ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("${formatAr(a.montant)} Ar", style = MaterialTheme.typography.titleMedium, color = AlertRed)
                        Text("Employé #${a.employeId}")
                        a.motif?.let { Text(it) }
                    }
                }
            }
        }
    }
    if (showDialog) AddAvanceDialog(employes, onDismiss = { showDialog = false }, onAdd = { e, m, mot ->
        vm.add(e, m, mot); showDialog = false
    })
}

@Composable
private fun AddAvanceDialog(employes: List<Utilisateur>, onDismiss: () -> Unit, onAdd: (Long, Long, String?) -> Unit) {
    var empId by remember { mutableStateOf<Long?>(null) }
    var montant by remember { mutableStateOf("") }
    var motif by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Nouvelle avance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                employes.forEach { e ->
                    FilterChip(selected = empId == e.idUtilisateur, onClick = { empId = e.idUtilisateur }, label = { Text(e.nom) })
                }
                OutlinedTextField(value = montant, onValueChange = { montant = it.filter { c -> c.isDigit() } },
                    label = { Text("Montant") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = motif, onValueChange = { motif = it }, label = { Text("Motif (optionnel)") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val e = empId ?: return@TextButton
                val m = montant.toLongOrNull() ?: return@TextButton
                onAdd(e, m, motif.ifBlank { null })
            }) { Text("Ajouter") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
