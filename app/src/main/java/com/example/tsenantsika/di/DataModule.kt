package com.example.tsenantsika.di

import androidx.room.Room
import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.repositories.*
import com.example.tsenantsika.network.EventBus
import org.koin.dsl.module

val dataModule = module {

    single {
        Room.databaseBuilder(
            get(),
            BoutiqueDatabase::class.java,
            BoutiqueDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { EventBus() }
    single { UtilisateurRepository(get()) }
    single { CategoriePrixRepository(get(), get()) }
    single { ParametreCommissionRepository(get(), get()) }
    single { LigneVenteRepository(get()) }
    single { AvanceEmployeRepository(get(), get()) }
    single { DepenseBoutiqueRepository(get(), get()) }
    single { AuditLogRepository(get()) }
    single { AuditRepository(get()) }
    single { StatsRepository(get()) }
    single { VenteRepository(get(), get(), get(), get()) }
    single { JourneeRepository(get(), get(), get()) }
    single { SyncRepository(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
