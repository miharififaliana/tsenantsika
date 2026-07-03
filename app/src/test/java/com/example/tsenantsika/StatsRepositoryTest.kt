package com.example.tsenantsika

import com.example.tsenantsika.data.repositories.JourneeStats
import org.junit.Assert.assertEquals
import org.junit.Test

class StatsRepositoryTest {

    @Test
    fun journeeStats_beneficeNet_calculation() {
        val stats = JourneeStats(chiffreAffaires = 100_000, totalDepenses = 20_000, beneficeNet = 80_000)
        assertEquals(80_000, stats.beneficeNet)
    }
}
