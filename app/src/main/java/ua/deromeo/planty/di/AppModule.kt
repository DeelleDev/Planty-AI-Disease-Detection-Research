package ua.deromeo.planty.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.deromeo.planty.BuildConfig
import ua.deromeo.planty.data.local.AppDatabase
import ua.deromeo.planty.data.local.dao.DiseaseDao
import ua.deromeo.planty.data.local.dao.FavoriteDao
import ua.deromeo.planty.data.local.dao.HistoryDao
import ua.deromeo.planty.data.local.dao.PlantDao
import ua.deromeo.planty.data.local.preferences.SearchHistoryRepositoryImpl
import ua.deromeo.planty.data.local.preferences.UserPreferencesImpl
import ua.deromeo.planty.data.remote.FirebaseDataSourceImpl
import ua.deromeo.planty.data.remote.RemoteDataSource
import ua.deromeo.planty.data.remote.RetrofitClient
import ua.deromeo.planty.data.remote.WeatherApiService
import ua.deromeo.planty.data.repository.PlantRepositoryImpl
import ua.deromeo.planty.data.repository.WeatherRepositoryImpl
import ua.deromeo.planty.domain.repository.PlantRepository
import ua.deromeo.planty.domain.repository.PreferencesRepository
import ua.deromeo.planty.domain.repository.SearchHistoryRepository
import ua.deromeo.planty.domain.repository.WeatherRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext, AppDatabase::class.java, "planty-db"
        ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    fun providePlantDao(database: AppDatabase): PlantDao {
        return database.plantDao()
    }

    @Provides
    fun provideDiseaseDao(database: AppDatabase): DiseaseDao {
        return database.diseaseDao()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    fun provideHistoryDao(database: AppDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun providePlantRepository(
        @ApplicationContext appContext: Context, db: AppDatabase, remoteDataSource: RemoteDataSource
    ): PlantRepository {
        return PlantRepositoryImpl(appContext, db, remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(): WeatherApiService {
        return RetrofitClient.weatherApiService
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApiService: WeatherApiService
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherApiService)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return UserPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(
        @ApplicationContext context: Context
    ): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(): RemoteDataSource {
        return FirebaseDataSourceImpl()
    }

    @Provides
    @Singleton
    @Named("WeatherApiKey")
    fun provideWeatherApiKey(): String {
        return BuildConfig.WEATHER_API_KEY
    }
}
