package com.example.tsenantsika.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsenantsika.data.DatabaseSeeder
import com.example.tsenantsika.data.repositories.AuthRepository
import com.example.tsenantsika.data.repositories.CategoriePrixRepository
import com.example.tsenantsika.data.repositories.ParametreCommissionRepository
import com.example.tsenantsika.data.repositories.UtilisateurRepository
import com.example.tsenantsika.ui.common.SnackbarBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LoginUiState(val loading: Boolean = false, val error: String? = null, val success: Boolean = false, val needsPatronne: Boolean = false)
data class PinUiState(val error: String? = null, val success: Boolean = false)

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val categorieRepo: CategoriePrixRepository,
    private val commissionRepo: ParametreCommissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _pinState = MutableStateFlow(PinUiState())
    val pinState: StateFlow<PinUiState> = _pinState.asStateFlow()

    init { checkPatronne() }

    private fun checkPatronne() {
        viewModelScope.launch {
            if (utilisateurRepository.countPatronne() == 0) {
                _uiState.value = LoginUiState(needsPatronne = true)
            }
        }
    }

    fun login(nom: String, pin: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(loading = true)
            when (val result = authRepository.login(nom, pin)) {
                is com.example.tsenantsika.data.repositories.AuthResult.Success ->
                    _uiState.value = LoginUiState(success = true)
                is com.example.tsenantsika.data.repositories.AuthResult.Error ->
                    _uiState.value = LoginUiState(error = result.message)
            }
        }
    }

    fun createPatronne(nom: String, pin: String) {
        viewModelScope.launch {
            when (val result = authRepository.createPatronne(nom, pin)) {
                is com.example.tsenantsika.data.repositories.AuthResult.Success -> {
                    DatabaseSeeder.seedDefaults(categorieRepo, commissionRepo, result.user.idUtilisateur)
                    _uiState.value = LoginUiState(success = true)
                }
                is com.example.tsenantsika.data.repositories.AuthResult.Error ->
                    _uiState.value = LoginUiState(error = result.message)
            }
        }
    }

    fun changePin(userId: Long, oldPin: String, newPin: String) {
        viewModelScope.launch {
            when (val result = authRepository.changePin(userId, oldPin, newPin)) {
                is com.example.tsenantsika.data.repositories.AuthResult.Success ->
                    _pinState.value = PinUiState(success = true).also { SnackbarBus.show("PIN modifié") }
                is com.example.tsenantsika.data.repositories.AuthResult.Error ->
                    _pinState.value = PinUiState(error = result.message)
            }
        }
    }

    fun setError(msg: String) { _uiState.value = LoginUiState(error = msg) }
}
