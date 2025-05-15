package com.ericg.neatflix.data.remote

import com.ericg.neatflix.data.preferences.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences,
    @Named("LaravelBaseUrl") private val laravelApiBaseUrl: String 
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrlString = request.url.toString()

        if (!requestUrlString.startsWith(laravelApiBaseUrl)) {
            return chain.proceed(request) 
        }

        val path = request.url.encodedPathSegments.joinToString("/")
        // Check if the path is exactly 'api/login' or 'api/register'
        // This assumes LARAVEL_API_BASE_URL ends with a '/'
        val isLoginOrRegisterPath = path == "api/login" || path == "api/register"

        if (isLoginOrRegisterPath) {
            return chain.proceed(request) 
        }

        val token = runBlocking { 
            userPreferences.authTokenFlow.firstOrNull()
        }

        return if (token != null) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }
    }
} 