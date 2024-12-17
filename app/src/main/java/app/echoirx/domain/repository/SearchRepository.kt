package app.echoirx.domain.repository

import app.echoirx.domain.model.SearchResult
import app.echoirx.presentation.screens.search.SearchFilter
import app.echoirx.presentation.screens.search.SearchType

interface SearchRepository {
    suspend fun search(query: String, type: SearchType): List<SearchResult>
    suspend fun getAlbumTracks(albumId: Long): List<SearchResult>
    suspend fun filterSearchResults(
        results: List<SearchResult>,
        filter: SearchFilter
    ): List<SearchResult>
}