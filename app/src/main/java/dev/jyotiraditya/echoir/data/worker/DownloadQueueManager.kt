package dev.jyotiraditya.echoir.data.worker

import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadQueueManager @Inject constructor(
    private val workManager: WorkManager
) {
    private val _activeDownloads = MutableStateFlow(0)
    val activeDownloads = _activeDownloads.asStateFlow()

    companion object {
        const val MAX_CONCURRENT_DOWNLOADS = 2
    }

    suspend fun canStartNewDownload(): Boolean {
        return _activeDownloads.value < MAX_CONCURRENT_DOWNLOADS
    }

    suspend fun incrementActiveDownloads() {
        _activeDownloads.value += 1
    }

    suspend fun decrementActiveDownloads() {
        _activeDownloads.value = (_activeDownloads.value - 1).coerceAtLeast(0)
    }
}