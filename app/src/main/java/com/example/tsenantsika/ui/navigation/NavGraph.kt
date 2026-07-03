package com.example.tsenantsika.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tsenantsika.data.entities.Role
import com.example.tsenantsika.network.client.NetworkClient
import com.example.tsenantsika.network.client.ServiceDiscoveryClient
import com.example.tsenantsika.network.server.BoutiqueServerService
import com.example.tsenantsika.ui.admin.EmployeManagementScreen
import com.example.tsenantsika.ui.auth.*
import com.example.tsenantsika.ui.employe.EmployeHomeScreen
import com.example.tsenantsika.ui.patronne.*
import com.example.tsenantsika.data.repositories.SyncRepository
import com.example.tsenantsika.ui.patronne.vente.VenteCreationScreen
import com.example.tsenantsika.ui.settings.SettingsScreen
import com.example.tsenantsika.ui.patronne.vente.VenteEditionScreen
import com.example.tsenantsika.ui.patronne.vente.VenteListScreen
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.tsenantsika.utils.Session
import com.example.tsenantsika.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

@Composable
fun BoutiqueNavGraph(
    sessionManager: SessionManager = koinInject(),
    discoveryClient: ServiceDiscoveryClient = koinInject(),
    networkClient: NetworkClient = koinInject(),
    syncRepository: SyncRepository = koinInject()
) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var session by remember { mutableStateOf<Session?>(null) }
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        session = sessionManager.sessionFlow.first()
        checked = true
    }

    if (!checked) return

    val start = when {
        session == null -> Routes.LOGIN
        session!!.role == Role.PATRONNE -> Routes.PATRONNE_HOME
        else -> Routes.EMPLOYE_HOME
    }

    LaunchedEffect(session) {
        session?.let { s ->
            if (s.role == Role.PATRONNE) BoutiqueServerService.start(context)
            else {
                discoveryClient.startDiscovery()
                syncRepository.startListening()
                launch {
                    discoveryClient.serverIp.collect { ip ->
                        ip?.let {
                            networkClient.connect(it)
                            syncRepository.initialSync()
                        }
                    }
                }
            }
        }
    }

    NavHost(navController, startDestination = start) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    runBlocking { session = sessionManager.sessionFlow.first() }
                    val dest = if (session?.role == Role.PATRONNE) Routes.PATRONNE_HOME else Routes.EMPLOYE_HOME
                    navController.navigate(dest) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onCreatePatronne = { navController.navigate(Routes.CREATE_PATRONNE) }
            )
        }
        composable(Routes.CREATE_PATRONNE) {
            CreatePatronneScreen(onSuccess = {
                runBlocking { session = sessionManager.sessionFlow.first() }
                navController.navigate(Routes.PATRONNE_HOME) { popUpTo(0) { inclusive = true } }
            })
        }
        composable(Routes.PATRONNE_HOME) {
            PatronneHomeScreen(
                onNavigate = { navController.navigate(it) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onLogout = {
                    runBlocking { sessionManager.clearSession() }
                    BoutiqueServerService.stop(context)
                    session = null
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable(Routes.EMPLOYE_HOME) {
            val userId = session?.userId ?: 0L
            EmployeHomeScreen(
                userId = userId,
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onLogout = {
                    runBlocking { sessionManager.clearSession() }
                    networkClient.disconnect()
                    discoveryClient.stopDiscovery()
                    session = null
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable(Routes.SETTINGS) {
            val userId = session?.userId ?: 0L
            val role = session?.role ?: Role.EMPLOYE
            SettingsScreen(
                userId = userId,
                role = role,
                onBack = { navController.popBackStack() },
                onNavigate = { navController.navigate(it) }
            )
        }
        composable(Routes.EMPLOYE_MANAGEMENT) {
            EmployeManagementScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.JOURNEE_HISTORIQUE) {
            JourneeHistoriqueScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.VENTE_LIST) {
            VenteListScreen(
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(Routes.VENTE_CREATE) },
                onEdit = { navController.navigate(Routes.venteEdit(it)) },
                userId = session?.userId ?: 0L
            )
        }
        composable(Routes.VENTE_CREATE) {
            VenteCreationScreen(userId = session?.userId ?: 0L, onDone = { navController.popBackStack() })
        }
        composable(
            Routes.VENTE_EDIT,
            arguments = listOf(navArgument("venteId") { type = NavType.LongType })
        ) { entry ->
            val venteId = entry.arguments?.getLong("venteId") ?: 0L
            VenteEditionScreen(venteId = venteId, userId = session?.userId ?: 0L, onDone = { navController.popBackStack() })
        }
        composable(Routes.AVANCES) {
            AvancesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.DEPENSES) {
            DepensesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ADMIN_SETTINGS) {
            AdminSettingsScreen(userId = session?.userId ?: 0L, onBack = { navController.popBackStack() })
        }
        composable(Routes.AUDIT) {
            AuditScreen(onBack = { navController.popBackStack() })
        }
    }
}
