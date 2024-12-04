package dev.jyotiraditya.echoir.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.jyotiraditya.echoir.data.local.dao.DownloadDao
import dev.jyotiraditya.echoir.data.media.FFmpegProcessor
import dev.jyotiraditya.echoir.data.media.MetadataManager
import dev.jyotiraditya.echoir.data.notification.DownloadNotificationManager
import dev.jyotiraditya.echoir.data.permission.PermissionsManager
import dev.jyotiraditya.echoir.data.remote.api.ApiService
import dev.jyotiraditya.echoir.data.repository.DownloadRepositoryImpl
import dev.jyotiraditya.echoir.data.repository.SearchRepositoryImpl
import dev.jyotiraditya.echoir.data.repository.SettingsRepositoryImpl
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import dev.jyotiraditya.echoir.domain.repository.SearchRepository
import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSearchRepository(
        apiService: ApiService
    ): SearchRepository = SearchRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideFFmpegProcessor(): FFmpegProcessor = FFmpegProcessor()

    @Provides
    @Singleton
    fun provideMetadataManager(
        apiService: ApiService,
    ): MetadataManager = MetadataManager(apiService)

    @Provides
    @Singleton
    fun providePermissionsManager(
        @ApplicationContext context: Context
    ): PermissionsManager = PermissionsManager(context)

    @Provides
    @Singleton
    fun provideDownloadNotificationManager(
        @ApplicationContext context: Context
    ): DownloadNotificationManager = DownloadNotificationManager(context)

    @Provides
    @Singleton
    fun provideDownloadRepository(
        apiService: ApiService,
        downloadDao: DownloadDao,
        settingsRepository: SettingsRepository,
        ffmpegProcessor: FFmpegProcessor,
        metadataManager: MetadataManager,
        @ApplicationContext context: Context
    ): DownloadRepository = DownloadRepositoryImpl(
        apiService = apiService,
        downloadDao = downloadDao,
        settingsRepository = settingsRepository,
        ffmpegProcessor = ffmpegProcessor,
        metadataManager = metadataManager,
        context = context
    )

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepositoryImpl(context)
}