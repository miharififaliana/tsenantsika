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
import com.example.tsenantsika.data.entities.DepenseBoutique
import com.example.tsenantsika.data.repositories.DepenseBoutiqueRepository
import com.example.tsenantsika.data.repositories.JourneeRepository
import com.example.tsenantsika.ui.common.BoutiqueTopBar
import com.example.tsenantsika.ui.common.formatAr
import com.example.tsenantsika.ui.theme.AlertRed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class DepensesViewModel(
    private val depenseRepo: DepenseBoutiqueRepository,
    private val journeeRepo: JourneeRepository
) : ViewModel() {
    var journeeId by mutableStateOf<Long?>(null); private set
    private val _depenses = MutableStateFlow<List<DepenseBoutique>>(emptyList())
    val depenses: StateFlow<List<DepenseBoutique>> = _depenses.asStateFlow()

    init {
        viewModelScope.launch {
            journeeId = journeeRepo.getJourneeOuverte()?.idJournee
            journeeId?.let { id -> depenseRepo.getDepensesByJournee(id).collect { _depenses.value = it } }
        }
    }

    fun add(libelle: String, montant: Long) {
        val jId = journeeId ?: return
        viewModelScope.launch {
            depenseRepo.addDepense(DepenseBoutique(libelle = libelle, montant = montant, journeeId = jId), 0L)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepensesScreen(onBack: () -> Unit, vm: DepensesViewModel = koinViewModel()) {
    val depenses by vm.depenses.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BoutiqueTopBar("Dépenses boutique", actions = { TextButton(onClick = onBack) { Text("Retour") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(depenses) { d ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(d.libelle, style = MaterialTheme.typography.titleMedium)
                        Text("${formatAr(d.montant)} Ar", color = AlertRed)
                    }
                }
            }
        }
    }
    if (showDialog) {
        var libelle by remember { mutableStateOf("") }
        var montant by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false }, title = { Text("Nouvelle dépense") },
            text = {
                Column {
                    OutlinedTextField(value = libelle, onValueChange = { libelle = it }, label = { Text("Libellé") })
                    OutlinedTextField(value = montant, onValueChange = { montant = it.filter { c -> c.isDigit() } },
                        label = { Text("Montant") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    montant.toLongOrNull()?.let { vm.add(libelle, it); showDialog = false }
                }) { Text("Ajouter") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Annuler") } }
        )
    }
}
