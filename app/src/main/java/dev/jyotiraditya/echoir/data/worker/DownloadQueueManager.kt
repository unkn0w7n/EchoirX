package dev.jyotiraditya.echoir.data.worker

import dev.jyotiraditya.echoir.domain.model.QueuedDownload
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadQueueManager @Inject constructor() {
    private val _activeDownloads = MutableStateFlow(0)
    private val _downloadQueue = MutableStateFlow<List<QueuedDownload>>(emptyList())

    companion object {
        const val MAX_CONCURRENT_DOWNLOADS = 2
    }

    fun canStartNewDownload(): Boolean {
        return _activeDownloads.value < MAX_CONCURRENT_DOWNLOADS
    }

    fun queueDownload(queuedDownload: QueuedDownload) {
        _downloadQueue.value += queuedDownload
    }

    fun dequeueDownload(): QueuedDownload? {
        val currentQueue = _downloadQueue.value
        if (currentQueue.isEmpty()) return null

        val download = currentQueue.first()
        _downloadQueue.value = currentQueue.drop(1)
        return download
    }

    fun incrementActiveDownloads() {
        _activeDownloads.value += 1
    }

    fun decrementActiveDownloads() {
        _activeDownloads.value = (_activeDownloads.value - 1).coerceAtLeast(0)
    }
}