package com.khang.nitflex.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.khang.nitflex.data.remote.APIService
import com.khang.nitflex.model.Film
import com.khang.nitflex.util.FilmType
import retrofit2.HttpException
import java.io.IOException

class RecommendedFilmSource(
    private val api: APIService,
    val filmId: Int,
    private val filmType: FilmType
) : PagingSource<Int, Film>() {
    override fun getRefreshKey(state: PagingState<Int, Film>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        return try {
            val nextPage = params.key ?: 1
            val recommendedMovies = if (filmType == FilmType.MOVIE) api.getRecommendedMovies(
                page = nextPage, movieId = filmId
            ) else api.getRecommendedTvShows(page = nextPage, filmId = filmId)

            LoadResult.Page(
                data = recommendedMovies.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (recommendedMovies.results.isEmpty()) null else recommendedMovies.page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}