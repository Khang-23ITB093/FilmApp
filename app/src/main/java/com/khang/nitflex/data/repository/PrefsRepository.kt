package com.khang.nitflex.data.repository

import com.khang.nitflex.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PrefsRepository @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend fun updateIncludeAdult(includeAdult: Boolean){
        userPreferences.updateIncludeAdult(includeAdult)
    }
    fun readIncludeAdult(): Flow<Boolean?> = userPreferences.includeAdultFlow
}