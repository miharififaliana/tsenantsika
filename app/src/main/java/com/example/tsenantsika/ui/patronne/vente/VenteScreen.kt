package com.example.tsenantsika.ui.patronne.vente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsenantsika.data.entities.CategoriePrix
import com.example.tsenantsika.data.entities.Role
import com.example.tsenantsika.data.entities.Utilisateur
import com.example.tsenantsika.data.entities.Vente
import com.example.tsenantsika.data.repositories.*
import com.example.tsenantsika.ui.common.*
import com.example.tsenantsika.ui.theme.AlertRed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

data class PanierLineUi(val categorieId: Long, val libelle: String, val prixRef: Long, val quantite: Int)

class VenteViewModel(
    private val venteRepo: VenteRepository,
    private val journeeRepo: JourneeRepository,
    private val categorieRepo: CategoriePrixRepository,
    private val utilisateurRepo: UtilisateurRepository
) : ViewModel() {
    val categories = categorieRepo.getCategoriesActives().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val employes = utilisateurRepo.getEmployesActifs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    var journeeId by mutableStateOf<Long?>(null); private set
    var error by mutableStateOf<String?>(null); private set
    var success by mutableStateOf(false); private set

    init {
        viewModelScope.launch { journeeId = journeeRepo.getJourneeOuverte()?.idJournee }
    }

    fun getVentes(journeeId: Long) = venteRepo.getVentesByJournee(journeeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createVente(vendeurId: Long, lignes: List<LignePanier>, prixFinal: Long, userId: Long) {
        val jId = journeeId ?: return
        viewModelScope.launch {
            runCatching { venteRepo.createVente(vendeurId, jId, lignes, prixFinal, userId) }
                .onSuccess { success = true }.onFailure { error = it.message }
        }
    }

    fun deleteVente(id: Long, motif: String, userId: Long) {
        viewModelScope.launch {
            runCatching { venteRepo.deleteVente(id, motif, userId) }.onFailure { error = it.message }
        }
    }

    fun updateVente(venteId: Long, vendeurId: Long, lignes: List<LignePanier>, prixFinal: Long, userId: Long) {
        viewModelScope.launch {
            runCatching { venteRepo.updateVente(venteId, vendeurId, lignes, prixFinal, userId) }
                .onSuccess { success = true }.onFailure { error = it.message }
        }
    }

    suspend fun loadLignes(venteId: Long) = venteRepo.getLignes(venteId)

    suspend fun getVente(venteId: Long) = venteRepo.getVenteById(venteId)

    fun resetSuccess() { success = false }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenteListScreen(onBack: () -> Unit, onCreate: () -> Unit, onEdit: (Long) -> Unit, userId: Long, vm: VenteViewModel = koinViewModel()) {
    val jId = vm.journeeId
    val ventes = if (jId != null) vm.getVentes(jId).collectAsState().value else emptyList()
    var deleteId by remember { mutableStateOf<Long?>(null) }
    var motif by remember { mutableStateOf("") }

    Scaffold(
        topBar = { BoutiqueTopBar("Ventes du jour", actions = { TextButton(onClick = onBack) { Text("Retour") } }) },
        floatingActionButton = { FloatingActionButton(onClick = onCreate) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        if (ventes.isEmpty()) EmptyState("Aucune vente")
        else LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ventes) { v ->
                Card(onClick = { onEdit(v.idVente) }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("${formatAr(v.prixTotalFacture)} Ar", style = MaterialTheme.typography.titleMedium)
                            Text("${v.nombrePiecesTotal} pièces • Commission ${formatAr(v.commissionTotale)} Ar")
                        }
                        IconButton(onClick = { deleteId = v.idVente }) {
                            Icon(Icons.Default.Delete, null, tint = AlertRed)
                        }
                    }
                }
            }
        }
    }
    deleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text("Supprimer la vente") },
            text = {
                OutlinedTextField(value = motif, onValueChange = { motif = it }, label = { Text("Motif obligatoire") })
            },
            confirmButton = {
                TextButton(onClick = { vm.deleteVente(id, motif, userId); deleteId = null; motif = "" }) { Text("Supprimer") }
            },
            dismissButton = { TextButton(onClick = { deleteId = null }) { Text("Annuler") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenteCreationScreen(userId: Long, onDone: () -> Unit, vm: VenteViewModel = koinViewModel()) {
    val categories by vm.categories.collectAsState()
    val employes by vm.employes.collectAsState()
    var vendeurId by remember { mutableStateOf<Long?>(null) }
    val lignes = remember { mutableStateListOf<PanierLineUi>() }
    var prixFinal by remember { mutableStateOf("") }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedEmp by remember { mutableStateOf(false) }

    val sousTotal = lignes.sumOf { it.prixRef * it.quantite }
    LaunchedEffect(sousTotal) { if (prixFinal.isEmpty()) prixFinal = sousTotal.toString() }

    LaunchedEffect(vm.success) { if (vm.success) { vm.resetSuccess(); onDone() } }

    Scaffold(topBar = { BoutiqueTopBar("Nouvelle vente", actions = { TextButton(onClick = onDone) { Text("Annuler") } }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ExposedDropdownMenuBox(expanded = expandedEmp, onExpandedChange = { expandedEmp = it }) {
                OutlinedTextField(
                    value = employes.find { it.idUtilisateur == vendeurId }?.nom ?: "Sélectionner vendeur",
                    onValueChange = {}, readOnly = true, modifier = Modifier.menuAnchor().fillMaxWidth(),
                    label = { Text("Vendeur") }
                )
                ExposedDropdownMenu(expanded = expandedEmp, onDismissRequest = { expandedEmp = false }) {
                    employes.forEach { e ->
                        DropdownMenuItem(text = { Text(e.nom) }, onClick = { vendeurId = e.idUtilisateur; expandedEmp = false })
                    }
                }
            }
            lignes.forEachIndexed { i, line ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(line.libelle)
                            Text("${line.quantite} x ${formatAr(line.prixRef)} = ${formatAr(line.prixRef * line.quantite)} Ar")
                        }
                        IconButton(onClick = { lignes.removeAt(i) }) { Icon(Icons.Default.Delete, null) }
                    }
                }
            }
            ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = it }) {
                OutlinedTextField(
                    value = "Ajouter catégorie", onValueChange = {}, readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(), label = { Text("Catégorie") }
                )
                ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                    categories.forEach { c ->
                        DropdownMenuItem(text = { Text("${c.libelle} - ${formatAr(c.prixReference)} Ar") }, onClick = {
                            lignes.add(PanierLineUi(c.idCategorie, c.libelle, c.prixReference, 1))
                            expandedCat = false
                        })
                    }
                }
            }
            lignes.forEachIndexed { i, line ->
                OutlinedTextField(
                    value = line.quantite.toString(),
                    onValueChange = { v -> v.toIntOrNull()?.let { if (it > 0) lignes[i] = line.copy(quantite = it) } },
                    label = { Text("Qté ${line.libelle}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Text("Sous-total: ${formatAr(sousTotal)} Ar", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = prixFinal, onValueChange = { prixFinal = it.filter { c -> c.isDigit() } },
                label = { Text("Prix final négocié") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            PrimaryButton("Valider la vente", onClick = {
                val vId = vendeurId ?: return@PrimaryButton
                val pf = prixFinal.toLongOrNull() ?: return@PrimaryButton
                vm.createVente(vId, lignes.map {
                    LignePanier(it.categorieId, it.prixRef, it.quantite, it.prixRef * it.quantite)
                }, pf, userId)
            }, enabled = lignes.isNotEmpty() && vendeurId != null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenteEditionScreen(venteId: Long, userId: Long, onDone: () -> Unit, vm: VenteViewModel = koinViewModel()) {
    val categories by vm.categories.collectAsState()
    val employes by vm.employes.collectAsState()
    var vendeurId by remember { mutableStateOf<Long?>(null) }
    val lignes = remember { mutableStateListOf<PanierLineUi>() }
    var prixFinal by remember { mutableStateOf("") }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedEmp by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(venteId, categories) {
        if (!loaded && categories.isNotEmpty()) {
            val vente = vm.getVente(venteId)
            vendeurId = vente?.vendeurId
            prixFinal = vente?.prixTotalFacture?.toString() ?: ""
            vm.loadLignes(venteId).forEach { l ->
                val cat = categories.find { it.idCategorie == l.categorieId }
                lignes.add(PanierLineUi(l.categorieId, cat?.libelle ?: "Cat", l.prixReferenceHistorique, l.quantite))
            }
            loaded = true
        }
    }

    LaunchedEffect(vm.success) { if (vm.success) { vm.resetSuccess(); onDone() } }

    Scaffold(topBar = { BoutiqueTopBar("Modifier vente", actions = { TextButton(onClick = onDone) { Text("Annuler") } }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ExposedDropdownMenuBox(expanded = expandedEmp, onExpandedChange = { expandedEmp = it }) {
                OutlinedTextField(
                    value = employes.find { it.idUtilisateur == vendeurId }?.nom ?: "Vendeur",
                    onValueChange = {}, readOnly = true, modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedEmp, onDismissRequest = { expandedEmp = false }) {
                    employes.forEach { e ->
                        DropdownMenuItem(text = { Text(e.nom) }, onClick = { vendeurId = e.idUtilisateur; expandedEmp = false })
                    }
                }
            }
            lignes.forEachIndexed { i, line ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("${line.libelle}: ${line.quantite} x ${formatAr(line.prixRef)}", Modifier.weight(1f))
                    IconButton(onClick = { lignes.removeAt(i) }) { Icon(Icons.Default.Delete, null) }
                }
            }
            ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = it }) {
                OutlinedTextField(value = "Ajouter ligne", onValueChange = {}, readOnly = true, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                    categories.forEach { c ->
                        DropdownMenuItem(text = { Text(c.libelle) }, onClick = {
                            lignes.add(PanierLineUi(c.idCategorie, c.libelle, c.prixReference, 1))
                            expandedCat = false
                        })
                    }
                }
            }
            OutlinedTextField(value = prixFinal, onValueChange = { prixFinal = it.filter { c -> c.isDigit() } },
                label = { Text("Prix final") }, modifier = Modifier.fillMaxWidth())
            PrimaryButton("Enregistrer", onClick = {
                val vId = vendeurId ?: return@PrimaryButton
                val pf = prixFinal.toLongOrNull() ?: return@PrimaryButton
                vm.updateVente(venteId, vId, lignes.map {
                    LignePanier(it.categorieId, it.prixRef, it.quantite, it.prixRef * it.quantite)
                }, pf, userId)
            })
        }
    }
}
