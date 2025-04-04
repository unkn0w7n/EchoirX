package app.echoirx.data.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.ParcelFileDescriptor
import android.system.Os
import android.util.Log
import app.echoirx.data.remote.api.ApiService
import com.kyant.taglib.Picture
import com.kyant.taglib.TagLib
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataManager @Inject constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "MetadataManager"
    }

    suspend fun embedMetadata(filePath: String, metadata: Map<String, String>) {
        try {
            val file = File(filePath)

            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE).use { pfd ->
                // Write basic metadata
                val propertyMap = HashMap<String, Array<String>>().apply {
                    metadata.forEach { (key, value) ->
                        if (key != "COVER") {
                            put(key, arrayOf(value))
                        }
                    }
                }
                TagLib.savePropertyMap(pfd.dup().detachFd(), propertyMap)

                // Write cover art if available
                metadata["COVER"]?.let { coverUrl ->
                    val imageData = apiService.downloadFile(coverUrl)
                    if (isValidImageData(imageData)) {
                        val picture = createPicture(imageData)
                        TagLib.savePictures(pfd.dup().detachFd(), arrayOf(picture))
                    }
                }

                Os.close(pfd.fileDescriptor)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error embedding metadata", e)
            throw e
        }
    }

    suspend fun extractAndSaveCoverArt(coverUrl: String?, outputPath: String): Boolean {
        if (coverUrl.isNullOrEmpty()) return false

        return try {
            val imageData = apiService.downloadFile(coverUrl)
            if (isValidImageData(imageData)) {
                val imageFile = File(outputPath)
                FileOutputStream(imageFile).use { fos ->
                    fos.write(imageData)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting cover art", e)
            false
        }
    }

    fun extractAndSaveLyrics(metadata: Map<String, String>, outputPath: String): Boolean {
        val lyrics = metadata["LYRICS"] ?: metadata["UNSYNCEDLYRICS"] ?: return false

        if (lyrics.isBlank()) return false

        return try {
            val lyricsFile = File(outputPath)
            lyricsFile.writeText(lyrics)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting lyrics", e)
            false
        }
    }

    suspend fun downloadCoverArt(coverUrl: String?): ByteArray? {
        if (coverUrl.isNullOrEmpty()) return null

        return try {
            val imageData = apiService.downloadFile(coverUrl)
            if (isValidImageData(imageData)) {
                imageData
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading cover art", e)
            null
        }
    }

    fun extractLyrics(metadata: Map<String, String>): String? {
        val lyrics = metadata["LYRICS"] ?: metadata["UNSYNCEDLYRICS"]
        return if (lyrics.isNullOrBlank()) null else lyrics
    }

    private fun isValidImageData(data: ByteArray): Boolean =
        try {
            BitmapFactory.decodeByteArray(data, 0, data.size) != null
        } catch (e: Exception) {
            false
        }

    private fun createPicture(imageData: ByteArray): Picture =
        ByteArrayOutputStream().use { stream ->
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            Picture(
                data = stream.toByteArray(),
                description = "Front Cover",
                pictureType = "Front Cover",
                mimeType = "image/jpeg"
            )
        }
}