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
import io.ktor.client.request.prepareGet
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.OutputStream
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

    suspend fun getDownloadInfo(request: PlaybackRequest): Pair<PlaybackResponseDto, Map<String, String>> =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()

            coroutineScope {
                val playback = async {
                    client.post("$BASE_URL/track/playback") {
                        contentType(ContentType.Application.Json)
                        header("X-API-Key", API_KEY)
                        setBody(request.copy(country = region))
                    }.body<PlaybackResponseDto>()
                }

                val metadata = async {
                    client.get("$BASE_URL/track/metadata") {
                        parameter("id", request.id)
                        parameter("country", region)
                        header("X-API-Key", API_KEY)
                    }.body<Map<String, String>>()
                }

                Pair(playback.await(), metadata.await())
            }
        }

    suspend fun downloadFile(url: String): ByteArray =
        withContext(Dispatchers.IO) {
            client.get(url) {
                header("X-API-Key", API_KEY)
            }.body()
        }

    suspend fun downloadFileToStream(url: String, outputStream: OutputStream) {
        withContext(Dispatchers.IO) {
            client.prepareGet(url) {
                header("X-API-Key", API_KEY)
            }.execute { it.bodyAsChannel().copyTo(outputStream) }
        }
    }
}