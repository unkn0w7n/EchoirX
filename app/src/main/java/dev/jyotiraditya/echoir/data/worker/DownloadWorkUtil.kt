package dev.jyotiraditya.echoir.data.worker

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.QualityConfig

fun enqueueDownloadWork(
    workManager: WorkManager,
    download: Download,
    config: QualityConfig,
    queueManager: DownloadQueueManager
) {
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