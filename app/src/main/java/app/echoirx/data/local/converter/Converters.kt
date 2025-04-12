package app.echoirx.data.local.converter

import androidx.room.TypeConverter
import app.echoirx.domain.model.DownloadStatus
import app.echoirx.domain.model.SearchResult
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromDownloadStatus(value: DownloadStatus): String = value.name

    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus = DownloadStatus.valueOf(value)

    @TypeConverter
    fun fromSearchResult(value: SearchResult): String = json.encodeToString(value)

    @TypeConverter
    fun toSearchResult(value: String): SearchResult = json.decodeFromString(value)
}