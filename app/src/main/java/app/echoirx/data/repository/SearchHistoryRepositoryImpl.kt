package app.echoirx.data.repository

import app.echoirx.data.local.dao.SearchHistoryDao
import app.echoirx.domain.model.SearchHistoryItem
import app.echoirx.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getRecentSearches(limit: Int): Flow<List<SearchHistoryItem>> {
        return searchHistoryDao.getRecentSearches(limit)
    }

    override suspend fun addSearch(query: String, type: String) {
        if (query.isBlank()) return

        val item = SearchHistoryItem(
            query = query.trim(),
            type = type
        )
        searchHistoryDao.insertSearch(item)
    }

    override suspend fun deleteSearch(query: String, type: String) {
        searchHistoryDao.deleteSearch(query, type)
    }

    override suspend fun clearHistory() {
        searchHistoryDao.clearSearchHistory()
    }

    override suspend fun searchHistory(query: String, limit: Int): List<SearchHistoryItem> {
        return searchHistoryDao.searchHistory(query, limit)
    }
}