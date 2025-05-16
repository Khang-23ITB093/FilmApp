package com.khang.nitflex.data.repository

import com.khang.nitflex.data.local.MyListMovie
import com.khang.nitflex.data.local.WatchListDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WatchListRepository @Inject constructor(private val database: WatchListDatabase) {
    suspend fun addToWatchList(movie: MyListMovie){
        database.moviesDao.addToWatchList(movie)
    }

    suspend fun exists(mediaId: Int): Int{
        return database.moviesDao.exists(mediaId)
    }

    suspend fun removeFromWatchList(mediaId: Int){
        database.moviesDao.removeFromWatchList(mediaId)
    }

    fun getFullWatchList(): Flow<List<MyListMovie>> {
       return database.moviesDao.getFullWatchList()
    }

    suspend fun deleteWatchList(){
        database.moviesDao.deleteWatchList()
    }
}