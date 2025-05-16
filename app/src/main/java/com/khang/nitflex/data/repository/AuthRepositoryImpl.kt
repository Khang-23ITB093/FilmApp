package com.khang.nitflex.data.repository

import com.khang.nitflex.data.remote.AuthApiService
import com.khang.nitflex.model.AuthResponse
import com.khang.nitflex.model.GeneralErrorResponse
import com.khang.nitflex.model.LoginRequest
import com.khang.nitflex.model.RegisterRequest
import com.khang.nitflex.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val gson: Gson // Inject Gson for parsing error body
) : AuthRepository {

    override suspend fun registerUser(registerRequest: RegisterRequest): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = authApiService.register(registerRequest)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = gson.fromJson(errorBody, GeneralErrorResponse::class.java)
                val errorMessage = errorResponse?.message ?: "Registration failed"
                // You might want to extract specific field errors from errorResponse.errors
                emit(Resource.Error(errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override suspend fun loginUser(loginRequest: LoginRequest): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = authApiService.login(loginRequest)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string()
                // Attempt to parse with GeneralErrorResponse for Laravel's validation messages
                val errorResponse = try {
                    gson.fromJson(errorBody, GeneralErrorResponse::class.java)
                } catch (e: Exception) { null }

                val errorMessage = if (errorResponse?.errors?.containsKey("email") == true && response.code() == 422) { // Specifically check for 422 for validation errors
                    errorResponse.errors["email"]?.joinToString() ?: errorResponse.message ?: "Invalid credentials."
                } else {
                    errorResponse?.message ?: "Login failed. Invalid credentials or server error."
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
} 