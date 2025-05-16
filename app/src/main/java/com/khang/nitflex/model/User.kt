package com.khang.nitflex.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) 