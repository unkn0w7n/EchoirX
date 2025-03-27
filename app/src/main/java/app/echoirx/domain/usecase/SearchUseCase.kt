package app.echoirx.domain.usecase

import app.echoirx.domain.model.PlaybackResponse
import app.echoirx.domain.model.SearchResult
import app.echoirx.domain.repository.SearchRepository
import app.echoirx.presentation.screens.search.SearchFilter
import app.echoirx.presentation.screens.search.SearchType
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend fun searchTracks(query: String): List<SearchResult> =
        repository.search(query, SearchType.TRACKS)

    suspend fun searchAlbums(query: String): List<SearchResult> =
        repository.search(query, SearchType.ALBUMS)

    suspend fun filterSearchResults(
        results: List<SearchResult>,
        filter: SearchFilter
    ): List<SearchResult> = repository.filterSearchResults(results, filter)

    suspend fun getTrackPreview(trackId: Long): PlaybackResponse =
        repository.getTrackPreview(trackId)
}