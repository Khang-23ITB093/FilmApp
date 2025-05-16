package com.khang.nitflex.data.remote.response

import com.khang.nitflex.model.Genre
import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)