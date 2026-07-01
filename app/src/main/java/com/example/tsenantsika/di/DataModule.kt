package com.example.tsenantsika.di

import androidx.room.Room
import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.repositories.*
import org.koin.dsl.module

val dataModule = module {

    // 1. Base de données Room (singleton)
    single {
        Room.databaseBuilder(
            get(),                    // Context via Koin
            BoutiqueDatabase::class.java,
            BoutiqueDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Pour le développement (supprime la DB en cas de changement de schéma)
            .build()
    }

    // 2. Repositories (singletons)
    single { UtilisateurRepository(get()) }
    single { JourneeRepository(get()) }
    single { CategoriePrixRepository(get()) }
    single { ParametreCommissionRepository(get()) }
    single { VenteRepository(get()) }
    single { LigneVenteRepository(get()) }           // À créer si pas encore fait
    single { AvanceEmployeRepository(get()) }
    single { DepenseBoutiqueRepository(get()) }
    single { AuditLogRepository(get()) }
}