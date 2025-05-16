package com.khang.nitflex.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.khang.nitflex.data.remote.APIService
import com.khang.nitflex.model.Search
import com.khang.nitflex.paging.SearchFilmSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val api: APIService
) {
    fun multiSearch(searchParams: String, includeAdult: Boolean): Flow<PagingData<Search>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SearchFilmSource(api = api, searchParams = searchParams, includeAdult)
            }
        ).flow
    }
}