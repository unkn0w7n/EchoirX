package dev.jyotiraditya.echoir.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jyotiraditya.echoir.data.remote.api.ApiService
import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    @Provides
    @Singleton
    fun provideApiService(
        client: HttpClient,
        settingsRepository: SettingsRepository
    ): ApiService = ApiService(client, settingsRepository)
}