package com.ericg.neatflix.data.remote

import com.ericg.neatflix.model.AuthResponse
import com.ericg.neatflix.model.LoginRequest
import com.ericg.neatflix.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiService {

    @Headers("Accept: application/json")
    @POST("api/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @POST("api/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

     @POST("api/logout")
     suspend fun logout(): Response<Unit>
} 