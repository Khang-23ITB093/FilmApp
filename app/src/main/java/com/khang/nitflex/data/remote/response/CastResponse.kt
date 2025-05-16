package com.khang.nitflex.data.remote.response

import com.khang.nitflex.model.Cast
import com.google.gson.annotations.SerializedName

data class CastResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val castResult: List<Cast>
)
