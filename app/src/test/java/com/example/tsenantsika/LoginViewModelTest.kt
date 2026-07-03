package com.example.tsenantsika

import com.example.tsenantsika.data.repositories.AuthRepository
import com.example.tsenantsika.data.repositories.CategoriePrixRepository
import com.example.tsenantsika.data.repositories.ParametreCommissionRepository
import com.example.tsenantsika.data.repositories.UtilisateurRepository
import com.example.tsenantsika.ui.auth.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_checks_patronne_exists() = runTest {
        val utilisateurRepo = mock<UtilisateurRepository>()
        whenever(utilisateurRepo.countPatronne()).thenReturn(1)
        val vm = LoginViewModel(mock(), utilisateurRepo, mock(), mock())
        assertNotNull(vm.uiState.value)
    }
}
