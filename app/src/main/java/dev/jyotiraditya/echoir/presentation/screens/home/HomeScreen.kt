package dev.jyotiraditya.echoir.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.presentation.components.EmptyStateMessage
import dev.jyotiraditya.echoir.presentation.screens.home.components.DownloadItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error ?: "Unknown error occurred",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        state.activeDownloads.isEmpty() && state.downloadHistory.isEmpty() -> {
            EmptyStateMessage(
                title = "No downloads yet",
                description = "Your downloaded tracks will appear here.",
                painter = painterResource(R.drawable.ic_download)
            )
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.activeDownloads.isNotEmpty()) {
                    item {
                        Text(
                            text = "Active Downloads",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(
                        items = state.activeDownloads,
                        key = { it.downloadId }
                    ) { download ->
                        DownloadItem(download = download)
                    }
                }

                if (state.downloadHistory.isNotEmpty()) {
                    item {
                        Text(
                            text = "Download History",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = if (state.activeDownloads.isNotEmpty()) 16.dp else 8.dp,
                                bottom = 8.dp
                            )
                        )
                    }

                    items(
                        items = state.downloadHistory,
                        key = { it.downloadId }
                    ) { download ->
                        DownloadItem(download = download)
                    }
                }
            }
        }
    }
}