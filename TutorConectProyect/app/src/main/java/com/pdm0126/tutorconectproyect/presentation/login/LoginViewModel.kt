package com.tutorconnect.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.core.utils.Validators
import com.tutorconnect.data.model.UserRole
import com.tutorconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.EmailChanged ->
                _uiState.update { it.copy(email = action.value, emailError = null, generalError = null) }
            is LoginUiAction.PasswordChanged ->
                _uiState.update { it.copy(password = action.value, passwordError = null, generalError = null) }
            is LoginUiAction.FullNameChanged ->
                _uiState.update { it.copy(fullName = action.value) }
            is LoginUiAction.RoleSelected ->
                _uiState.update { it.copy(selectedRole = action.role) }
            LoginUiAction.ToggleMode ->
                _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, generalError = null) }
            LoginUiAction.Submit -> submit()
            LoginUiAction.MicrosoftLogin -> microsoftLogin()
            LoginUiAction.ForgotPassword -> viewModelScope.launch {
                _events.send(LoginUiEvent.ShowMessage("Te enviaremos un enlace de recuperación."))
            }
        }
    }

    private fun submit() {
        val state = _uiState.value
        val emailError = if (!Validators.isInstitutionalEmail(state.email))
            "Usa tu correo institucional (@uca.edu.sv)." else null
        val passwordError = if (!Validators.isValidPassword(state.password))
            "Mínimo 6 caracteres." else null

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.login(state.email, state.password)
            _uiState.update { it.copy(isLoading = false) }
            result
                .onSuccess { _events.send(LoginUiEvent.NavigateToDashboard) }
                .onFailure { e -> _uiState.update { it.copy(generalError = e.message) } }
        }
    }

    private fun microsoftLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.loginWithMicrosoft()
            _uiState.update { it.copy(isLoading = false) }
            result
                .onSuccess { _events.send(LoginUiEvent.NavigateToDashboard) }
                .onFailure { e -> _uiState.update { it.copy(generalError = e.message) } }
        }
    }
}
