package com.khang.nitflex.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.khang.nitflex.data.remote.APIService
import com.khang.nitflex.model.Film
import com.khang.nitflex.util.FilmType
import retrofit2.HttpException
import java.io.IOException

class NowPlayingFilmSource(private val api: APIService, private val filmType: FilmType) :
    PagingSource<Int, Film>() {
    override fun getRefreshKey(state: PagingState<Int, Film>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        return try {
            val nextPage = params.key ?: 1
            val nowPlayingMovies =
                if (filmType == FilmType.MOVIE) api.getNowPlayingMovies(page = nextPage)
                else api.getOnTheAirTvShows(page = nextPage)

            LoadResult.Page(
                data = nowPlayingMovies.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (nowPlayingMovies.results.isEmpty()) null else nowPlayingMovies.page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}