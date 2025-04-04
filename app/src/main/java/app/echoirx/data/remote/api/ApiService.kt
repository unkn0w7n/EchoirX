package app.echoirx.data.remote.api

import app.echoirx.data.remote.dto.PlaybackResponseDto
import app.echoirx.data.remote.dto.SearchResultDto
import app.echoirx.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
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
    private suspend fun getBaseUrl(): String = settingsRepository.getServerUrl()

    suspend fun search(query: String, type: String): List<SearchResultDto> =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()
            val baseUrl = getBaseUrl()

            client.get("$baseUrl/search") {
                parameter("query", query)
                parameter("type", type)
                parameter("country", region)
            }.body()
        }

    suspend fun getAlbumTracks(albumId: Long): List<SearchResultDto> =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()
            val baseUrl = getBaseUrl()

            client.get("$baseUrl/album/tracks") {
                parameter("id", albumId)
                parameter("country", region)
            }.body()
        }

    suspend fun getDownloadInfo(
        trackId: Long,
        quality: String
    ): Pair<PlaybackResponseDto, Map<String, String>> =
        withContext(Dispatchers.IO) {
            val region = settingsRepository.getRegion()
            val baseUrl = getBaseUrl()

            coroutineScope {
                val playback = async {
                    client.get("$baseUrl/track/playback") {
                        parameter("id", trackId)
                        parameter("quality", quality)
                        parameter("country", region)
                    }.body<PlaybackResponseDto>()
                }

                val metadata = async {
                    client.get("$baseUrl/track/metadata") {
                        parameter("id", trackId)
                        parameter("country", region)
                    }.body<Map<String, String>>()
                }

                Pair(playback.await(), metadata.await())
            }
        }

    suspend fun getTrackPreview(trackId: Long): PlaybackResponseDto =
        withContext(Dispatchers.IO) {
            val baseUrl = getBaseUrl()

            client.get("$baseUrl/track/preview") {
                parameter("id", trackId)
            }.body()
        }

    suspend fun downloadFile(url: String): ByteArray =
        withContext(Dispatchers.IO) {
            client.get(url).body()
        }

    suspend fun downloadFileToStream(url: String, outputStream: OutputStream) {
        withContext(Dispatchers.IO) {
            client.prepareGet(url).execute {
                it.bodyAsChannel().copyTo(outputStream)
            }
        }
    }
}