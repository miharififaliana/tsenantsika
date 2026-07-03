package com.example.tsenantsika

import com.example.tsenantsika.data.repositories.AuthRepository
import com.example.tsenantsika.data.repositories.UtilisateurRepository
import com.example.tsenantsika.utils.SessionManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuthRepositoryTest {

    private lateinit var utilisateurRepo: UtilisateurRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        utilisateurRepo = mock()
        sessionManager = mock()
        authRepository = AuthRepository(utilisateurRepo, sessionManager)
    }

    @Test
    fun login_rejects_invalid_pin_length() = runBlocking {
        val result = authRepository.login("Test", "12")
        assertTrue(result is com.example.tsenantsika.data.repositories.AuthResult.Error)
    }

    @Test
    fun login_rejects_wrong_credentials() = runBlocking {
        whenever(utilisateurRepo.findByNomAndPin("Test", "1234")).thenReturn(null)
        val result = authRepository.login("Test", "1234")
        assertTrue(result is com.example.tsenantsika.data.repositories.AuthResult.Error)
    }
}
