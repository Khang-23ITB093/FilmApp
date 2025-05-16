package com.khang.nitflex.model
 
data class AuthResponse(
    val message: String,
    val user: User,
    val token: String
) 