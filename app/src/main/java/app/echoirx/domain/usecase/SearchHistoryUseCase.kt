package app.echoirx.domain.usecase

import app.echoirx.domain.model.SearchHistoryItem
import app.echoirx.domain.repository.SearchHistoryRepository
import app.echoirx.presentation.screens.search.SearchType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryItem>> =
        repository.getRecentSearches(limit)

    suspend fun addSearch(query: String, type: SearchType) {
        if (query.isBlank()) return
        repository.addSearch(query.trim(), type.name)
    }

    suspend fun deleteSearch(item: SearchHistoryItem) =
        repository.deleteSearch(item.id)

    suspend fun clearHistory() =
        repository.clearHistory()

    suspend fun searchHistory(query: String, limit: Int = 10): List<SearchHistoryItem> =
        repository.searchHistory(query, limit)
}