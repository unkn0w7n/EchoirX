package dev.jyotiraditya.echoir.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.data.utils.extensions.openAudioFile
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.presentation.components.TrackCover
import java.util.Locale

@Composable
fun DownloadItem(
    download: Download,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val qualityText = when (download.quality) {
        "HI_RES_LOSSLESS" -> stringResource(QualityConfig.HiRes.label)
        "LOSSLESS" -> stringResource(QualityConfig.Lossless.label)
        "DOLBY_ATMOS" -> if (download.isAc4) {
            stringResource(QualityConfig.DolbyAtmosAC4.label)
        } else {
            stringResource(QualityConfig.DolbyAtmosAC3.label)
        }

        "HIGH" -> stringResource(QualityConfig.AAC320.label)
        "LOW" -> stringResource(QualityConfig.AAC96.label)
        else -> stringResource(R.string.label_unknown)
    }.uppercase(Locale.getDefault())

    ListItem(
        modifier = modifier.then(
            if (download.status == DownloadStatus.COMPLETED && !download.filePath.isNullOrEmpty()) {
                Modifier.clickable {
                    download.filePath.openAudioFile(context)
                }
            } else {
                Modifier
            }
        ),
        overlineContent = {
            Text(
                text = qualityText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        },
        headlineContent = {
            Text(
                text = download.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        supportingContent = {
            Text(
                text = download.artist,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingContent = {
            TrackCover(
                url = download.cover,
                size = 56.dp
            )
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when (download.status) {
                    DownloadStatus.QUEUED -> {
                        Text(
                            text = stringResource(R.string.label_queued),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    DownloadStatus.DOWNLOADING -> {
                        CircularProgressIndicator(
                            progress = { download.progress / 100f },
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    DownloadStatus.MERGING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    DownloadStatus.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Outlined.Done,
                            contentDescription = stringResource(R.string.action_done),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    DownloadStatus.FAILED -> {
                        Icon(
                            imageVector = Icons.Outlined.Error,
                            contentDescription = stringResource(R.string.cd_error),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = download.duration,
                    style = MaterialTheme.typography.bodySmall
                )
                if (download.explicit) {
                    Icon(
                        painter = painterResource(R.drawable.ic_explicit),
                        contentDescription = stringResource(R.string.cd_explicit_content),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    )
}