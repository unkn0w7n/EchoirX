package app.echoirx.presentation.screens.home

import app.echoirx.domain.model.Download

data class HomeState(
    val activeDownloads: List<Download> = emptyList(),
    val downloadHistory: List<Download> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)