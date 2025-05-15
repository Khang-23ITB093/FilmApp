package com.ericg.neatflix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ericg.neatflix.data.preferences.UserPreferences
import com.ericg.neatflix.data.repository.AuthRepository
import com.ericg.neatflix.model.AuthResponse
import com.ericg.neatflix.model.LoginRequest
import com.ericg.neatflix.model.RegisterRequest
import com.ericg.neatflix.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // StateFlow for registration process
    private val _registrationState = MutableStateFlow<Resource<AuthResponse>>(Resource.Initial()) 
    val registrationState: StateFlow<Resource<AuthResponse>> = _registrationState.asStateFlow()

    // StateFlow for login process
    private val _loginState = MutableStateFlow<Resource<AuthResponse>>(Resource.Initial()) 
    val loginState: StateFlow<Resource<AuthResponse>> = _loginState.asStateFlow()

    // StateFlow for current auth token (to check if user is logged in)
    val authToken: StateFlow<String?> = userPreferences.authTokenFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Added userName StateFlow
    val userName: StateFlow<String?> = userPreferences.userNameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)


    fun registerUser(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _registrationState.value = Resource.Loading()
            authRepository.registerUser(registerRequest).collect { result ->
                _registrationState.value = result
                if (result is Resource.Success && result.data?.token != null) {
                    userPreferences.saveAuthToken(result.data.token)
                    // Save user name on registration
                    result.data.user?.name?.let { userPreferences.saveUserName(it) }
                }
            }
        }
    }

    fun loginUser(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            authRepository.loginUser(loginRequest).collect { result ->
                _loginState.value = result
                if (result is Resource.Success && result.data?.token != null) {
                    userPreferences.saveAuthToken(result.data.token)
                    // Save user name on login
                    result.data.user?.name?.let { userPreferences.saveUserName(it) }
                }
            }
        }
    }
    
    fun logoutUser() {
        viewModelScope.launch {
            userPreferences.clearAuthToken()
            _loginState.value = Resource.Initial()
            _registrationState.value = Resource.Initial()
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = Resource.Initial()
    }

    fun resetLoginState() {
        _loginState.value = Resource.Initial()
    }
} 