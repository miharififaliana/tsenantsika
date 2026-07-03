package com.example.tsenantsika.di

import com.example.tsenantsika.data.repositories.*
import com.example.tsenantsika.network.EventBus
import com.example.tsenantsika.network.client.NetworkClient
import com.example.tsenantsika.network.client.ServiceDiscoveryClient
import com.example.tsenantsika.network.server.KtorServerManager
import com.example.tsenantsika.network.server.ServiceDiscovery
import com.example.tsenantsika.ui.admin.EmployeViewModel as AdminEmployeViewModel
import com.example.tsenantsika.ui.auth.LoginViewModel
import com.example.tsenantsika.ui.employe.EmployeViewModel
import com.example.tsenantsika.ui.patronne.*
import com.example.tsenantsika.ui.patronne.vente.VenteViewModel
import com.example.tsenantsika.utils.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SessionManager(androidContext()) }
    single { ServiceDiscovery(androidContext()) }
    single { ServiceDiscoveryClient(androidContext()) }
    single { NetworkClient(get()) }
    single {
        KtorServerManager(
            get(), get(), get(), get(), get(), get(), get(), get()
        )
    }
    single { AuthRepository(get(), get()) }

    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { JourneeViewModel(get(), get()) }
    viewModel { VenteViewModel(get(), get(), get(), get()) }
    viewModel { AvancesViewModel(get(), get(), get()) }
    viewModel { DepensesViewModel(get(), get()) }
    viewModel { AdminSettingsViewModel(get(), get(), get()) }
    viewModel { AuditViewModel(get()) }
    viewModel { AdminEmployeViewModel(get()) }
    viewModel { EmployeViewModel(get(), get(), get()) }
}
