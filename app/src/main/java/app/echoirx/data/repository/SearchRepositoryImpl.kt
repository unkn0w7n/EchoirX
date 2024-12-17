package app.echoirx.data.repository

import app.echoirx.data.remote.api.ApiService
import app.echoirx.data.remote.mapper.SearchResultMapper.toDomain
import app.echoirx.domain.model.SearchResult
import app.echoirx.domain.repository.SearchRepository
import app.echoirx.presentation.screens.search.SearchFilter
import app.echoirx.presentation.screens.search.SearchType
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {
    override suspend fun search(query: String, type: SearchType): List<SearchResult> =
        apiService.search(query, type.name.lowercase())
            .map { it.toDomain() }

    override suspend fun getAlbumTracks(albumId: Long): List<SearchResult> =
        apiService.getAlbumTracks(albumId)
            .map { it.toDomain() }

    override suspend fun filterSearchResults(
        results: List<SearchResult>,
        filter: SearchFilter
    ): List<SearchResult> {
        return results.filter { result ->
            val formatMatch = filter.qualities.isEmpty() || filter.qualities.any {
                result.formats?.contains(it.format) ?: false
            }
            val explicitMatch = filter.contentFilters.isEmpty() || filter.contentFilters.any {
                it.explicit == result.explicit
            }
            formatMatch && explicitMatch
        }
    }
}