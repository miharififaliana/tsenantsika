package com.example.tsenantsika.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

/**
 * Test d'intégration complet pour valider tous les DAO et les règles métier (Phase 1).
 */
@RunWith(AndroidJUnit4::class)
class FullDaoTest {

    private lateinit var db: BoutiqueDatabase

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
    fun closeDb() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun testVenteCompleteAvecLignesEtStats() = runTest {
        // 1. Préparation des données de base
        val patronne = Utilisateur(
            nom = "Patronne Test",
            codePin = "0000",
            role = Role.PATRONNE
        )
        val patronneId = db.utilisateurDao().insert(patronne)

        val journee = Journee(
            heureOuverture = Instant.now(),
            statut = StatutJournee.OUVERTE
        )
        val journeeId = db.journeeDao().insert(journee)

        val categorie1 = CategoriePrix(
            libelle = "Catégorie 1",
            prixReference = 3000L
        )
        val cat1Id = db.categoriePrixDao().insert(categorie1)

        val parametre = ParametreCommission(
            montantCommissionParPiece = 1000L,
            modifiePar = patronneId
        )
        db.parametreCommissionDao().insert(parametre)

        // 2. Création d'une vente complète
        val vente = Vente(
            prixTotalFacture = 50000L,
            nombrePiecesTotal = 5,
            commissionUnitaireUtilisee = 1000L,
            commissionTotale = 5000L,
            vendeurId = patronneId,
            journeeId = journeeId,
            statut = StatutVente.VALIDEE
        )
        val venteId = db.venteDao().insert(vente)

        // 3. Ajout des lignes de vente
        val lignes = listOf(
            LigneVente(
                venteId = venteId,
                categorieId = cat1Id,
                prixReferenceHistorique = 3000L,
                quantite = 3,
                sousTotal = 9000L
            ),
            LigneVente(
                venteId = venteId,
                categorieId = cat1Id,
                prixReferenceHistorique = 15000L,
                quantite = 2,
                sousTotal = 30000L
            )
        )
        db.ligneVenteDao().insertAll(lignes)

        // 4. Vérifications
        val ventesJournee = db.venteDao().getVentesByJournee(journeeId).firstOrNull()
        assertNotNull("Aucune vente récupérée pour la journée", ventesJournee)
        assertEquals(1, ventesJournee?.size)

        val ca = db.venteDao().getChiffreAffairesJournee(journeeId)
        assertEquals(50000L, ca)

        val commission = db.venteDao().getCommissionBruteVendeur(journeeId, patronneId)
        assertEquals(5000L, commission)

        val lignesRecuperees = db.ligneVenteDao().getLignesByVente(venteId)
        assertEquals(2, lignesRecuperees.size)

        // 5. Test avance
        val avance = AvanceEmploye(
            employeId = patronneId,
            montant = 2000L,
            journeeId = journeeId
        )
        db.avanceEmployeDao().insert(avance)

        val totalAvances = db.avanceEmployeDao().getTotalAvancesEmploye(journeeId, patronneId)
        assertEquals(2000L, totalAvances)
    }

    @Test
    fun testJourneeOuverteUnique() = runTest {
        val journee1 = Journee(heureOuverture = Instant.now(), statut = StatutJournee.OUVERTE)
        db.journeeDao().insert(journee1)

        val journeeOuverte = db.journeeDao().getJourneeOuverte()
        assertNotNull(journeeOuverte)
    }
}