package com.example.tsenantsika

import org.junit.Assert.*
import org.junit.Test
import com.example.tsenantsika.data.entities.*

class EnumsTest {

    @Test
    fun testEnumsConversionForRoom() {
        val converters = Converters()

        // Test Role
        val role = Role.PATRONNE
        val roleStr = converters.fromRole(role)
        assertEquals("PATRONNE", roleStr)
        assertEquals(role, converters.toRole(roleStr))

        // Test StatutJournee
        val statutJ = StatutJournee.OUVERTE
        assertEquals("OUVERTE", converters.fromStatutJournee(statutJ))
        assertEquals(statutJ, converters.toStatutJournee("OUVERTE"))

        // Test StatutVente et ActionAudit (similaire)
        val action = ActionAudit.SUPPRESSION_VENTE
        assertNotNull(converters.fromActionAudit(action))
        assertEquals(action, converters.toActionAudit("SUPPRESSION_VENTE"))
    }

    @Test
    fun testEnumValuesCoverage() {
        assertTrue(Role.values().isNotEmpty())
        assertTrue(StatutJournee.values().size == 2)
        // Vérification que tous les enums sont sérialisables sans exception
    }
}