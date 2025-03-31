package app.echoirx.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.echoirx.data.remote.api.ApiService
import app.echoirx.data.remote.mapper.PlaybackMapper.toDomain
import app.echoirx.data.remote.mapper.SearchResultMapper.toDomain
import app.echoirx.domain.model.PlaybackResponse
import app.echoirx.domain.model.SearchResult
import app.echoirx.domain.repository.SearchRepository
import app.echoirx.presentation.screens.search.SearchFilter
import app.echoirx.presentation.screens.search.SearchType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

private val Context.rateLimitDataStore: DataStore<Preferences> by preferencesDataStore(name = "rate_limit")

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : SearchRepository {

    private object PreferencesKeys {
        val IS_RATE_LIMITED = booleanPreferencesKey("is_rate_limited")
        val RATE_LIMIT_START_TIME = longPreferencesKey("rate_limit_start_time")
    }

    override val isRateLimited: Flow<Boolean> = context.rateLimitDataStore.data.map { preferences ->
        val isLimited = preferences[PreferencesKeys.IS_RATE_LIMITED] ?: false
        val startTime = preferences[PreferencesKeys.RATE_LIMIT_START_TIME] ?: 0L

        if (isLimited) {
            val limitTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = startTime
            }
            val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            if (currentTime.get(Calendar.DAY_OF_YEAR) != limitTime.get(Calendar.DAY_OF_YEAR) ||
                currentTime.get(Calendar.YEAR) != limitTime.get(Calendar.YEAR)
            ) {
                setRateLimited(false)
                return@map false
            }
            true
        } else {
            false
        }
    }

    override suspend fun setRateLimited(limited: Boolean) {
        context.rateLimitDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_RATE_LIMITED] = limited
            if (limited) {
                preferences[PreferencesKeys.RATE_LIMIT_START_TIME] = System.currentTimeMillis()
            } else {
                preferences.remove(PreferencesKeys.RATE_LIMIT_START_TIME)
            }
        }
    }

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
            val formatMatch = filter.qualities.isEmpty() || filter.qualities.any { quality ->
                result.formats?.let { formats -> quality.format in formats } ?: false
            }
            val explicitMatch = filter.contentFilters.isEmpty() || filter.contentFilters.any {
                it.explicit == result.explicit
            }
            formatMatch && explicitMatch
        }
    }

    override suspend fun getTrackPreview(trackId: Long): PlaybackResponse =
        apiService.getTrackPreview(trackId).toDomain()
}