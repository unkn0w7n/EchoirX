package app.echoirx.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.echoirx.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(item: SearchHistoryItem)

    @Query("DELETE FROM search_history WHERE query = :query AND type = :type")
    suspend fun deleteSearch(query: String, type: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("SELECT * FROM search_history WHERE query LIKE :query || '%' ORDER BY timestamp DESC LIMIT :limit")
    suspend fun searchHistory(query: String, limit: Int = 10): List<SearchHistoryItem>
}