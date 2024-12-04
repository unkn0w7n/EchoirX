package dev.jyotiraditya.echoir.domain.usecase

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.jyotiraditya.echoir.data.worker.DownloadQueueManager
import dev.jyotiraditya.echoir.data.worker.DownloadWorker
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.DownloadRequest
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import kotlinx.coroutines.delay
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
            explicit = request.track.explicit,
            isAc4 = request.config.ac4
        )

        downloadRepository.saveDownload(download)
        enqueueDownloadWork(download, request.config)
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
                isAc4 = request.config.ac4,
                albumId = request.album.id,
                albumTitle = request.album.title,
                albumDirectory = request.downloadContext.directory
            )

            downloadRepository.saveDownload(download)

            // Wait until we can start a new download
            while (!queueManager.canStartNewDownload()) {
                delay(1000)
            }

            enqueueDownloadWork(download, request.config)
        }
    }

    private suspend fun enqueueDownloadWork(download: Download, config: QualityConfig) {
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                workDataOf(
                    DownloadWorker.KEY_DOWNLOAD_ID to download.downloadId,
                    DownloadWorker.KEY_TRACK_ID to download.trackId,
                    DownloadWorker.KEY_QUALITY to config.quality,
                    DownloadWorker.KEY_AC4 to config.ac4,
                    DownloadWorker.KEY_IMMERSIVE to config.immersive
                )
            )
            .build()

        queueManager.incrementActiveDownloads()

        workManager.enqueueUniqueWork(
            "download_${download.downloadId}",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}