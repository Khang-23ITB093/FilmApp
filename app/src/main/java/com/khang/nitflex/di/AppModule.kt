package com.khang.nitflex.di

import android.app.Application
import androidx.room.Room
import com.khang.nitflex.data.local.WatchListDatabase
import com.khang.nitflex.data.preferences.UserPreferences
import com.khang.nitflex.data.remote.APIService
import com.khang.nitflex.data.remote.AuthApiService
import com.khang.nitflex.data.remote.AuthInterceptor
import com.khang.nitflex.data.repository.AuthRepository
import com.khang.nitflex.data.repository.AuthRepositoryImpl
import com.khang.nitflex.data.repository.GenreRepository
import com.khang.nitflex.data.repository.FilmRepository
import com.khang.nitflex.data.repository.SearchRepository
import com.khang.nitflex.data.repository.WatchListRepository
import com.khang.nitflex.util.Constants.BASE_URL
import com.khang.nitflex.util.Constants.LARAVEL_API_BASE_URL
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    @Named("LaravelBaseUrl") 
    fun provideLaravelBaseUrl(): String {
        return LARAVEL_API_BASE_URL
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(
        userPreferences: UserPreferences,
        @Named("LaravelBaseUrl") laravelBaseUrl: String 
    ): AuthInterceptor { 
        return AuthInterceptor(userPreferences, laravelBaseUrl)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor 
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor) 
            .callTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson { 
        return Gson()
    }

    @Singleton
    @Provides
    fun providesTmdbApiService(okHttpClient: OkHttpClient, gson: Gson): APIService { 
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) 
            .client(okHttpClient)
            .build()
            .create(APIService::class.java)
    }

    @Singleton
    @Provides
    fun providesAuthApiService(okHttpClient: OkHttpClient, gson: Gson): AuthApiService { 
        return Retrofit.Builder()
            .baseUrl(LARAVEL_API_BASE_URL) 
            .addConverterFactory(GsonConverterFactory.create(gson)) 
            .client(okHttpClient)
            .build()
            .create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(authApiService: AuthApiService, gson: Gson): AuthRepository {
        return AuthRepositoryImpl(authApiService, gson)
    }

    @Singleton
    @Provides
    fun provideMoviesRepository(api: APIService) = FilmRepository(api = api)

    @Singleton
    @Provides
    fun provideSearchRepository(api: APIService) = SearchRepository(api = api)

    @Singleton
    @Provides
    fun providesGenresRepository(api: APIService) = GenreRepository(api)

    @Singleton
    @Provides
    fun providesWatchListRepository(watchListDatabase: WatchListDatabase) =
        WatchListRepository(database = watchListDatabase)

    @Provides
    @Singleton
    fun providesWatchListDatabase(application: Application): WatchListDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            WatchListDatabase::class.java,
            "watch_list_table"
        ).fallbackToDestructiveMigration().build()
    }
    @Provides
    @Singleton
    fun providesDataStore(application: Application): UserPreferences{
        return UserPreferences(application.applicationContext)
    }
}
