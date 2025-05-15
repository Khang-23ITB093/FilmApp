package com.ericg.neatflix.model
 
data class AuthResponse(
    val message: String,
    val user: User,
    val token: String
) 