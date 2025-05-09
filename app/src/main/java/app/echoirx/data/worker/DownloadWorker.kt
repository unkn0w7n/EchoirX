package app.echoirx.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.echoirx.R
import app.echoirx.data.notification.DownloadNotificationManager
import app.echoirx.domain.model.DownloadStatus
import app.echoirx.domain.repository.DownloadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import java.util.UUID

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadRepository: DownloadRepository,
    private val notificationManager: DownloadNotificationManager,
    private val queueManager: DownloadQueueManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_DOWNLOAD_ID = "download_id"
        const val KEY_TRACK_ID = "track_id"
        const val KEY_QUALITY = "quality"
        const val KEY_PROGRESS = "progress"
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val downloadId =
            inputData.getString(KEY_DOWNLOAD_ID) ?: return createDefaultForegroundInfo()
        val download = downloadRepository.getDownloadById(downloadId)
        val title =
            download?.searchResult?.title ?: applicationContext.getString(R.string.label_unknown)
        val isMerging = download?.status == DownloadStatus.MERGING
        val progress = download?.progress ?: 0

        return notificationManager.createDownloadNotification(
            downloadId = downloadId,
            title = if (isMerging)
                applicationContext.getString(R.string.notification_processing, title)
            else
                applicationContext.getString(R.string.notification_downloading, title),
            progress = progress,
            indeterminate = isMerging
        )
    }

    private fun createDefaultForegroundInfo(): ForegroundInfo {
        return notificationManager.createDownloadNotification(
            downloadId = UUID.randomUUID().toString(),
            title = applicationContext.getString(R.string.notification_progress),
            progress = 0,
            indeterminate = false
        )
    }

    override suspend fun doWork(): Result {
        val downloadId = inputData.getString(KEY_DOWNLOAD_ID) ?: return Result.failure()
        val trackId = inputData.getLong(KEY_TRACK_ID, -1)
        if (trackId == -1L) return Result.failure()

        val quality = inputData.getString(KEY_QUALITY) ?: return Result.failure()

        return try {
            val download = downloadRepository.getDownloadById(downloadId)
            // Get modes from the SearchResult
            val modes = download?.searchResult?.modes

            val result = downloadRepository.processDownload(
                downloadId = downloadId,
                trackId = trackId,
                quality = quality,
                modes = modes,
                onProgress = { progress ->
                    setProgress(workDataOf(KEY_PROGRESS to progress))
                    downloadRepository.updateDownloadProgress(downloadId, progress)

                    // Update notification with current progress
                    download?.let {
                        notificationManager.updateDownloadProgress(
                            downloadId = downloadId,
                            title = it.searchResult.title,
                            progress = progress,
                            indeterminate = false
                        )
                    }
                }
            )

            if (result.isSuccess) {
                download?.let {
                    notificationManager.showCompletionNotification(
                        downloadId = downloadId,
                        title = it.searchResult.title
                    )
                }
                Result.success()
            } else {
                download?.let {
                    notificationManager.showErrorNotification(
                        downloadId = downloadId,
                        title = it.searchResult.title
                    )
                } ?: notificationManager.showErrorNotification(
                    downloadId = downloadId,
                    title = applicationContext.getString(R.string.label_unknown)
                )
                Result.failure()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            val download = downloadRepository.getDownloadById(downloadId)
            download?.let {
                notificationManager.showErrorNotification(
                    downloadId = downloadId,
                    title = it.searchResult.title
                )
            } ?: notificationManager.showErrorNotification(
                downloadId = downloadId,
                title = applicationContext.getString(R.string.label_unknown)
            )

            try {
                downloadRepository.updateDownloadStatus(downloadId, DownloadStatus.FAILED)
            } catch (_: Exception) {
                // Silently ignore database errors during cleanup
            }

            Result.failure()
        } finally {
            queueManager.decrementActiveDownloads()

            if (queueManager.canStartNewDownload()) {
                val nextDownload = queueManager.dequeueDownload()
                if (nextDownload != null) {
                    enqueueDownloadWork(
                        workManager = WorkManager.getInstance(applicationContext),
                        download = nextDownload.download,
                        config = nextDownload.config,
                        queueManager = queueManager
                    )
                }
            }
        }
    }
}