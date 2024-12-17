package app.echoirx.data.local.converter

import androidx.room.TypeConverter
import app.echoirx.domain.model.DownloadStatus

class Converters {
    @TypeConverter
    fun fromDownloadStatus(value: DownloadStatus): String = value.name

    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus = DownloadStatus.valueOf(value)
}
