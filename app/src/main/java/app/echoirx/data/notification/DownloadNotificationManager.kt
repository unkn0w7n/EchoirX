package app.echoirx.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import app.echoirx.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "download_channel"
        const val GROUP_KEY = "download_group"
        const val SUMMARY_ID = 0
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notification_channel_desc)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(
        title: String,
        contentText: String? = null,
        progress: Int? = null,
        indeterminate: Boolean = false,
        ongoing: Boolean = false,
        autoCancel: Boolean = false,
        category: String = NotificationCompat.CATEGORY_PROGRESS
    ) = NotificationCompat.Builder(context, CHANNEL_ID).apply {
        setContentTitle(title)
        setSmallIcon(R.drawable.ic_download)
        setGroup(GROUP_KEY)
        setCategory(category)
        setOngoing(ongoing)
        setAutoCancel(autoCancel)
        contentText?.let { setContentText(it) }
        progress?.let { setProgress(100, it, indeterminate) }
    }.build()

    fun createDownloadNotification(
        downloadId: String,
        title: String,
        progress: Int,
        indeterminate: Boolean
    ): ForegroundInfo {
        val notification = buildNotification(
            title = title,
            progress = progress,
            indeterminate = indeterminate,
            ongoing = true
        )
        updateSummaryNotification()
        return ForegroundInfo(downloadId.hashCode(), notification)
    }

    fun updateDownloadProgress(
        downloadId: String,
        title: String,
        progress: Int,
        indeterminate: Boolean
    ) {
        notificationManager.notify(
            downloadId.hashCode(),
            buildNotification(
                title = title,
                progress = progress,
                indeterminate = indeterminate,
                ongoing = true
            )
        )
        updateSummaryNotification()
    }

    fun showCompletionNotification(downloadId: String, title: String) {
        notificationManager.notify(
            downloadId.hashCode(),
            buildNotification(
                title = context.getString(R.string.notification_complete),
                contentText = title,
                autoCancel = true,
                category = NotificationCompat.CATEGORY_STATUS
            )
        )
        updateSummaryNotification()
    }

    fun showErrorNotification(downloadId: String, title: String) {
        notificationManager.notify(
            downloadId.hashCode(),
            buildNotification(
                title = context.getString(R.string.notification_failed),
                contentText = title,
                autoCancel = true,
                category = NotificationCompat.CATEGORY_ERROR
            )
        )
        updateSummaryNotification()
    }

    private fun updateSummaryNotification() {
        notificationManager.notify(
            SUMMARY_ID,
            buildNotification(
                title = context.getString(R.string.notification_progress),
                ongoing = true
            ).apply { flags = flags or NotificationCompat.FLAG_GROUP_SUMMARY }
        )
    }
}