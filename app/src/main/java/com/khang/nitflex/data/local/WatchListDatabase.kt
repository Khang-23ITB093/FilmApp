package com.khang.nitflex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [MyListMovie::class],
    exportSchema = true
)
abstract class WatchListDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
}