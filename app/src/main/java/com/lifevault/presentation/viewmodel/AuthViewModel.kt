package com.lifevault.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifevault.data.model.AuthState
import com.lifevault.data.model.User
import com.lifevault.data.model.UserProfile
import com.lifevault.data.repository.AuthRepository
import com.lifevault.data.repository.LifeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val userProfile: UserProfile? = null,
    val hasProfile: Boolean = false,
    val pendingPhoneNumber: String? = null,
    val isVerificationSent: Boolean = false,
    val verificationCode: String? = null // For demo purposes only
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val lifeRepository: LifeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            authRepository.getCurrentUser().collect { user ->
                if (user != null && user.isPhoneVerified) {
                    // Пользователь аутентифицирован, проверяем наличие профиля
                    lifeRepository.getUserProfile().collect { profile ->
                        _uiState.value = _uiState.value.copy(
                            isAuthenticated = true,
                            user = user,
                            userProfile = profile,
                            hasProfile = profile != null,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        user = user,
                        userProfile = null,
                        hasProfile = false,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun login(phoneNumber: String, password: String) {
        if (phoneNumber.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Заполните все поля")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            authRepository.login(phoneNumber, password)
                .onSuccess { user ->
                    // После успешного логина проверяем профиль
                    checkUserProfile(user)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }
    
    fun register(phoneNumber: String, password: String) {
        if (phoneNumber.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Заполните все поля")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            authRepository.register(phoneNumber, password)
                .onSuccess { user ->
                    // После успешной регистрации отправляем SMS
                    sendVerificationCode(user.phoneNumber)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }
    
    fun sendVerificationCode(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            authRepository.sendVerificationCode(phoneNumber)
                .onSuccess { code ->
                    _uiState.value = _uiState.value.copy(
                        isVerificationSent = true,
                        pendingPhoneNumber = phoneNumber,
                        verificationCode = code, // For demo - in production this would not be exposed
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }
    
    fun verifyCode(phoneNumber: String, code: String) {
        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Введите код подтверждения")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            authRepository.verifyCode(phoneNumber, code)
                .onSuccess {
                    // После подтверждения номера проверяем статус аутентификации
                    checkAuthStatus()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }
    
    fun resendVerificationCode(phoneNumber: String) {
        sendVerificationCode(phoneNumber)
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState() // Reset to initial state
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetVerificationState() {
        _uiState.value = _uiState.value.copy(
            isVerificationSent = false,
            pendingPhoneNumber = null,
            verificationCode = null
        )
    }
    
    // Для тестирования - очищает всех пользователей
    fun clearAllUsersForTesting() {
        viewModelScope.launch {
            authRepository.clearAllUsers()
            _uiState.value = AuthUiState() // Reset to initial state
        }
    }
    
    private suspend fun checkUserProfile(user: User) {
        lifeRepository.getUserProfile().collect { profile ->
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                user = user,
                userProfile = profile,
                hasProfile = profile != null,
                isLoading = false,
                error = null
            )
        }
    }
}