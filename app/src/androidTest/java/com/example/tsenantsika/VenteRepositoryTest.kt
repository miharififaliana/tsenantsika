package com.example.tsenantsika

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.*
import com.example.tsenantsika.data.repositories.JourneeRepository
import com.example.tsenantsika.data.repositories.VenteRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class VenteRepositoryTest {

    private lateinit var db: BoutiqueDatabase
    private lateinit var venteRepository: VenteRepository
    private lateinit var journeeRepository: JourneeRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            BoutiqueDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testCreateVenteAvecLignesEtStats() = runTest {
        // 1. Préparation des données de base
        val journee = Journee(
            heureOuverture = Instant.now(),
            statut = StatutJournee.OUVERTE
        )
        val journeeId = journeeRepository.insert(journee)

        val patronne = Utilisateur(
            nom = "Test Patronne",
            codePin = "1234",
            role = Role.PATRONNE
        )
        val vendeurId = db.utilisateurDao().insert(patronne)

        val categorie1 = CategoriePrix(
            libelle = "Catégorie Test",
            prixReference = 10000L
        )
        val cat1Id = db.categoriePrixDao().insert(categorie1)

        // 2.
        val vente = Vente(
            prixTotalFacture = 45000L,
            nombrePiecesTotal = 4,
            commissionUnitaireUtilisee = 1000L,
            commissionTotale = 4000L,
            vendeurId = vendeurId,
            journeeId = journeeId,
            statut = StatutVente.VALIDEE
        )

        // 3. Création via Repository (transaction interne)
        val lignes = listOf(
            LigneVente(
                categorieId = cat1Id.toLong(),
                prixReferenceHistorique = 10000L,
                quantite = 2,
                sousTotal = 20000L
            ),
            LigneVente(
                categorieId = cat1Id.toLong(),
                prixReferenceHistorique = 12500L,
                quantite = 2,
                sousTotal = 25000L
            )
        )

        val venteId = venteRepository.createVenteAvecLignes(vente, lignes)

        assertTrue("Vente ID invalide", venteId > 0)

        // 4. Vérification des statistiques
        val ca = venteRepository.getChiffreAffairesJournee(journeeId)
        assertEquals(45000L, ca)

        val ventes = venteRepository.getVentesByJournee(journeeId).firstOrNull()
        assertNotNull("Aucune vente récupérée", ventes)
        assertEquals(1, ventes?.size)

        // Vérification des lignes
        val lignesRecuperees = db.ligneVenteDao().getLignesByVente(venteId)
        assertEquals(2, lignesRecuperees.size)
    }
}