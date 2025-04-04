package app.echoirx.domain.usecase

import androidx.work.WorkManager
import app.echoirx.data.worker.DownloadQueueManager
import app.echoirx.data.worker.enqueueDownloadWork
import app.echoirx.domain.model.Download
import app.echoirx.domain.model.DownloadRequest
import app.echoirx.domain.model.QualityConfig
import app.echoirx.domain.model.QueuedDownload
import app.echoirx.domain.repository.DownloadRepository
import javax.inject.Inject

class ProcessDownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val workManager: WorkManager,
    private val queueManager: DownloadQueueManager
) {
    suspend operator fun invoke(request: DownloadRequest) {
        when (request) {
            is DownloadRequest.Track -> processTrackDownload(request)
            is DownloadRequest.Album -> processAlbumDownload(request)
        }
    }

    private suspend fun processTrackDownload(request: DownloadRequest.Track) {
        val download = Download(
            trackId = request.track.id,
            title = request.track.title,
            artist = request.track.artists.joinToString(", "),
            cover = request.track.cover,
            quality = request.config.quality,
            duration = request.track.duration,
            explicit = request.track.explicit
        )

        downloadRepository.saveDownload(download)
        queueManager.queueDownload(QueuedDownload(download, request.config))
        processQueue()
    }

    private suspend fun processAlbumDownload(request: DownloadRequest.Album) {
        request.tracks.forEach { track ->
            val download = Download(
                trackId = track.id,
                title = track.title,
                artist = track.artists.joinToString(", "),
                cover = track.cover,
                quality = request.config.quality,
                duration = track.duration,
                explicit = track.explicit,
                albumId = request.album.id,
                albumTitle = request.album.title,
                albumDirectory = request.downloadContext.directory
            )

            downloadRepository.saveDownload(download)
            queueManager.queueDownload(QueuedDownload(download, request.config))
        }
        processQueue()
    }

    private fun processQueue() {
        while (queueManager.canStartNewDownload()) {
            val nextDownload = queueManager.dequeueDownload() ?: break
            startDownloadWork(nextDownload.download, nextDownload.config)
        }
    }

    private fun startDownloadWork(download: Download, config: QualityConfig) {
        enqueueDownloadWork(
            workManager = workManager,
            download = download,
            config = config,
            queueManager = queueManager
        )
    }
}