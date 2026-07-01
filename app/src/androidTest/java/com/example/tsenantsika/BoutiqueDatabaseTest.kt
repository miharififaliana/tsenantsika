package com.example.tsenantsika
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.*
import com.example.tsenantsika.data.repositories.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class BoutiqueDatabaseTest {

    private lateinit var db: BoutiqueDatabase
    private lateinit var utilisateurRepo: UtilisateurRepository
    private lateinit var journeeRepo: JourneeRepository
    private lateinit var venteRepo: VenteRepository
    private lateinit var avanceRepo: AvanceEmployeRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            BoutiqueDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        utilisateurRepo = UtilisateurRepository(db)
        journeeRepo = JourneeRepository(db)
        venteRepo = VenteRepository(db)
        avanceRepo = AvanceEmployeRepository(db)
    }

    @After
    fun closeDb() {
        db.close()
    }

    // Scénario 1 : Gestion des utilisateurs et authentification
    @Test
    fun testGestionUtilisateursEtRoles() = runTest {
        // Given
        val patronne = Utilisateur(nom = "Ranja", codePin = "0000", role = Role.PATRONNE)
        val employe = Utilisateur(nom = "Fara", codePin = "1234", role = Role.EMPLOYE)

        // When
        val patronneId = utilisateurRepo.insert(patronne)
        val employeId = utilisateurRepo.insert(employe)

        // Then
        assertTrue(patronneId > 0)
        assertTrue(employeId > 0)
        assertEquals(1, utilisateurRepo.countPatronne()) // Une seule Patronne
    }

    // Scénario 2 : Cycle complet d'une journée avec vente multi-lignes
    @Test
    fun testCycleJourneeComplete() = runTest {
        // Given
        val journee = Journee(heureOuverture = Instant.now(), statut = StatutJournee.OUVERTE)
        val journeeId = journeeRepo.insert(journee)

        val patronne = Utilisateur(nom = "Ranja", codePin = "0000", role = Role.PATRONNE)
        val vendeurId = utilisateurRepo.insert(patronne)

        val vente = Vente(
            prixTotalFacture = 35000L,
            nombrePiecesTotal = 5,
            commissionUnitaireUtilisee = 1000L,
            commissionTotale = 5000L,
            vendeurId = vendeurId,
            journeeId = journeeId
        )

        val lignes = listOf(
            LigneVente(categorieId = 1, prixReferenceHistorique = 5000L, quantite = 3, sousTotal = 15000L),
            LigneVente(categorieId = 2, prixReferenceHistorique = 10000L, quantite = 2, sousTotal = 20000L)
        )

        // When
        val venteId = venteRepo.createVenteAvecLignes(vente, lignes)

        // Then
        assertTrue(venteId > 0)
        val ca = venteRepo.getChiffreAffairesJournee(journeeId)
        assertEquals(35000L, ca)
    }

    // Scénario 3 : Calculs financiers et historique
    @Test
    fun testCalculsCommissionsEtHistorique() = runTest {
        // Given
        val journeeId = journeeRepo.insert(Journee(heureOuverture = Instant.now()))
        val employeId = utilisateurRepo.insert(Utilisateur(nom = "Fara", codePin = "1234", role = Role.EMPLOYE))

        val vente = Vente(
            prixTotalFacture = 20000L,
            nombrePiecesTotal = 3,
            commissionUnitaireUtilisee = 1000L,
            commissionTotale = 3000L,
            vendeurId = employeId,
            journeeId = journeeId
        )
        val venteId = venteRepo.createVenteAvecLignes(vente, emptyList())

        val avance = AvanceEmploye(
            employeId = employeId,
            montant = 1000L,
            journeeId = journeeId
        )
        avanceRepo.insert(avance)

        // When / Then
        val commissionBrute = venteRepo.getCommissionBruteVendeur(journeeId, employeId)
        assertEquals(3000L, commissionBrute)

        val totalAvances = avanceRepo.getTotalAvancesEmploye(journeeId, employeId)
        assertEquals(1000L, totalAvances)

        // Reste à payer = commission - avances
        val resteAPayer = (commissionBrute ?: 0) - (totalAvances ?: 0)
        assertEquals(2000L, resteAPayer)
    }
}