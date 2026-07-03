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
import com.example.tsenantsika.data.entities.ActionAudit
import com.example.tsenantsika.data.entities.AuditLog
import com.example.tsenantsika.data.entities.CategoriePrix
import com.example.tsenantsika.data.entities.ParametreCommission
import com.example.tsenantsika.data.repositories.*
import com.example.tsenantsika.ui.common.BoutiqueTopBar
import com.example.tsenantsika.ui.common.formatAr
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Instant

class AdminSettingsViewModel(
    private val categorieRepo: CategoriePrixRepository,
    private val commissionRepo: ParametreCommissionRepository,
    private val auditRepo: AuditRepository
) : ViewModel() {
    val categories = categorieRepo.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    var commission by mutableStateOf<ParametreCommission?>(null); private set

    init { viewModelScope.launch { commission = commissionRepo.getDernierParametre() } }

    fun updateCategorie(cat: CategoriePrix, newPrix: Long, userId: Long) {
        viewModelScope.launch {
            val old = cat.prixReference
            categorieRepo.update(cat.copy(prixReference = newPrix))
            auditRepo.log(ActionAudit.MODIFICATION_CATEGORIE, userId, "Prix $old → $newPrix", cat.idCategorie)
        }
    }

    fun addCategorie(libelle: String, prix: Long) {
        viewModelScope.launch { categorieRepo.insert(CategoriePrix(libelle = libelle, prixReference = prix)) }
    }

    fun toggleCategorie(cat: CategoriePrix) {
        viewModelScope.launch { categorieRepo.update(cat.copy(actif = !cat.actif)) }
    }

    fun updateCommission(montant: Long, userId: Long) {
        viewModelScope.launch {
            val old = commission?.montantCommissionParPiece ?: 0
            commissionRepo.insert(ParametreCommission(montantCommissionParPiece = montant, modifiePar = userId))
            commission = commissionRepo.getDernierParametre()
            auditRepo.log(ActionAudit.MODIFICATION_COMMISSION, userId, "Commission $old → $montant")
        }
    }
}

class AuditViewModel(private val auditLogRepository: AuditLogRepository) : ViewModel() {
    val logs = auditLogRepository.getAllLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(userId: Long, onBack: () -> Unit, vm: AdminSettingsViewModel = koinViewModel()) {
    val categories by vm.categories.collectAsState()
    var newMontant by remember { mutableStateOf(vm.commission?.montantCommissionParPiece?.toString() ?: "1000") }
    var showAddCat by remember { mutableStateOf(false) }
    var editCat by remember { mutableStateOf<CategoriePrix?>(null) }

    Scaffold(
        topBar = { BoutiqueTopBar("Paramètres avancés", actions = { TextButton(onClick = onBack) { Text("Retour") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddCat = true }) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Commission unitaire", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = newMontant, onValueChange = { newMontant = it.filter { c -> c.isDigit() } },
                    label = { Text("Montant/pièce") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))
                TextButton(onClick = { newMontant.toLongOrNull()?.let { vm.updateCommission(it, userId) } }) {
                    Text("Mettre à jour commission")
                }
            }
            item { Text("Catégories de prix", style = MaterialTheme.typography.titleMedium) }
            items(categories) { cat ->
                Card(onClick = { editCat = cat }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(cat.libelle)
                            Text("${formatAr(cat.prixReference)} Ar")
                        }
                        Switch(checked = cat.actif, onCheckedChange = { vm.toggleCategorie(cat) })
                    }
                }
            }
        }
    }
    if (showAddCat) CategoryDialog("Nouvelle catégorie", onDismiss = { showAddCat = false }) { lib, prix ->
        vm.addCategorie(lib, prix); showAddCat = false
    }
    editCat?.let { cat ->
        CategoryDialog("Modifier ${cat.libelle}", initialPrix = cat.prixReference.toString(),
            onDismiss = { editCat = null }) { _, prix ->
            vm.updateCategorie(cat, prix, userId)
            editCat = null
        }
    }
}

@Composable
private fun CategoryDialog(
    title: String,
    initialLibelle: String = "",
    initialPrix: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    var libelle by remember { mutableStateOf(initialLibelle) }
    var prix by remember { mutableStateOf(initialPrix) }
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = libelle, onValueChange = { libelle = it }, label = { Text("Libellé") })
                OutlinedTextField(value = prix, onValueChange = { prix = it.filter { c -> c.isDigit() } },
                    label = { Text("Prix référence") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            TextButton(onClick = { prix.toLongOrNull()?.let { onConfirm(libelle, it) } }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreen(onBack: () -> Unit, vm: AuditViewModel = koinViewModel()) {
    val logs by vm.logs.collectAsState()
    var filter by remember { mutableStateOf<ActionAudit?>(null) }
    val filtered = if (filter == null) logs else logs.filter { it.action == filter }

    Scaffold(topBar = { BoutiqueTopBar("Journal d'audit", actions = { TextButton(onClick = onBack) { Text("Retour") } }) }) { padding ->
        Column(Modifier.padding(padding)) {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(selected = filter == null, onClick = { filter = null }, label = { Text("Tous") })
                FilterChip(selected = filter == ActionAudit.SUPPRESSION_VENTE, onClick = { filter = ActionAudit.SUPPRESSION_VENTE },
                    label = { Text("Suppressions") })
                FilterChip(selected = filter == ActionAudit.MODIFICATION_COMMISSION, onClick = { filter = ActionAudit.MODIFICATION_COMMISSION },
                    label = { Text("Commissions") })
            }
            LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { log ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(log.action.name, style = MaterialTheme.typography.titleSmall)
                            Text(log.details, style = MaterialTheme.typography.bodySmall)
                            Text(log.dateHeure.toString(), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
