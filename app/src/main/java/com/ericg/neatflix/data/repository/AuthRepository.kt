package com.ericg.neatflix.data.repository

import com.ericg.neatflix.model.AuthResponse
import com.ericg.neatflix.model.LoginRequest
import com.ericg.neatflix.model.RegisterRequest
import com.ericg.neatflix.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(registerRequest: RegisterRequest): Flow<Resource<AuthResponse>>
    suspend fun loginUser(loginRequest: LoginRequest): Flow<Resource<AuthResponse>>
    // suspend fun logoutUser(): Flow<Resource<Unit>> // Placeholder for logout
} 