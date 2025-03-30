package app.echoirx.domain.repository

import app.echoirx.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryItem>>
    suspend fun addSearch(query: String, type: String)
    suspend fun deleteSearch(query: String, type: String)
    suspend fun clearHistory()
    suspend fun searchHistory(query: String, limit: Int = 10): List<SearchHistoryItem>
}