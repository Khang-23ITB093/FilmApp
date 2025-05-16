package com.khang.nitflex.data.repository

import com.khang.nitflex.data.remote.response.GenreResponse
import com.khang.nitflex.data.remote.APIService
import com.khang.nitflex.util.FilmType
import com.khang.nitflex.util.Resource
import java.lang.Exception
import javax.inject.Inject

class GenreRepository @Inject constructor(private val api: APIService) {
    suspend fun getMoviesGenre(filmType: FilmType): Resource<GenreResponse>{
        val response = try {
            if (filmType == FilmType.MOVIE) api.getMovieGenres() else api.getTvShowGenres()
        } catch (e: Exception){
            return Resource.Error("Unknown error occurred!")
        }
        return Resource.Success(response)
    }
}