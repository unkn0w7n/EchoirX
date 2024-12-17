package app.echoirx.presentation.screens.details

import app.echoirx.domain.model.Download
import app.echoirx.domain.model.SearchResult

data class DetailsState(
    val item: SearchResult? = null,
    val tracks: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val downloads: Map<Long, Map<String, Download>> = emptyMap()
)