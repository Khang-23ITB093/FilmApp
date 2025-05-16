package com.khang.nitflex.data.repository

import com.khang.nitflex.model.AuthResponse
import com.khang.nitflex.model.LoginRequest
import com.khang.nitflex.model.RegisterRequest
import com.khang.nitflex.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(registerRequest: RegisterRequest): Flow<Resource<AuthResponse>>
    suspend fun loginUser(loginRequest: LoginRequest): Flow<Resource<AuthResponse>>
    // suspend fun logoutUser(): Flow<Resource<Unit>> // Placeholder for logout
} 