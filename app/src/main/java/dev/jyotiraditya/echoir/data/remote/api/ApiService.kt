package dev.jyotiraditya.echoir.data.remote.api

import dev.jyotiraditya.echoir.BuildConfig
import dev.jyotiraditya.echoir.data.remote.dto.PlaybackResponseDto
import dev.jyotiraditya.echoir.data.remote.dto.SearchResultDto
import dev.jyotiraditya.echoir.domain.model.PlaybackRequest
import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiService @Inject constructor(
    private val client: HttpClient,
    private val settingsRepository: SettingsRepository
) {
    companion object {
        private const val BASE_URL = "https://echoir.vercel.app/api"
        private const val API_KEY = BuildConfig.API_KEY
    }

    suspend fun search(query: String, type: String): List<SearchResultDto> =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()
            client.get("$BASE_URL/search") {
                parameter("query", query)
                parameter("type", type)
                parameter("country", region)
                header("X-API-Key", API_KEY)
            }.body()
        }

    suspend fun getAlbumTracks(albumId: Long): List<SearchResultDto> =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()
            client.get("$BASE_URL/album/tracks") {
                parameter("id", albumId)
                parameter("country", region)
                header("X-API-Key", API_KEY)
            }.body()
        }

    suspend fun getPlaybackInfo(request: PlaybackRequest): PlaybackResponseDto =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()
            client.post("$BASE_URL/track/playback") {
                contentType(ContentType.Application.Json)
                header("X-API-Key", API_KEY)
                setBody(request.copy(country = region))
            }.body()
        }

    suspend fun getTrackMetadata(trackId: Long): Map<String, String> =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()
            client.get("$BASE_URL/track/metadata") {
                parameter("id", trackId)
                parameter("country", region)
                header("X-API-Key", API_KEY)
            }.body()
        }

    suspend fun downloadFile(url: String): ByteArray =
        withContext(Dispatchers.IO) {
            client.get(url) {
                header("X-API-Key", API_KEY)
            }.body()
        }
}