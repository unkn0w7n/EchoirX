package app.echoirx.presentation.screens.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.echoirx.R
import app.echoirx.data.media.AudioPreviewPlayer
import app.echoirx.data.remote.api.CloudflareRateLimitException
import app.echoirx.domain.model.DownloadRequest
import app.echoirx.domain.model.QualityConfig
import app.echoirx.domain.model.SearchHistoryItem
import app.echoirx.domain.model.SearchResult
import app.echoirx.domain.repository.SearchRepository
import app.echoirx.domain.usecase.ProcessDownloadUseCase
import app.echoirx.domain.usecase.SearchHistoryUseCase
import app.echoirx.domain.usecase.SearchUseCase
import app.echoirx.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val searchHistoryUseCase: SearchHistoryUseCase,
    private val processDownloadUseCase: ProcessDownloadUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val audioPreviewPlayer: AudioPreviewPlayer,
    private val searchRepository: SearchRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    val isPreviewPlaying = audioPreviewPlayer.isPlaying

    init {
        loadSearchHistory()
        setupQueryListener()
        checkRateLimitState()
    }

    private fun checkRateLimitState() {
        viewModelScope.launch {
            searchRepository.isRateLimited.collect { isLimited ->
                if (isLimited) {
                    _state.update {
                        it.copy(
                            status = SearchStatus.RateLimitExceeded,
                            searchEnabled = false,
                            error = context.getString(R.string.cloudflare_rate_limit_message)
                        )
                    }
                } else {
                    if (_state.value.status == SearchStatus.RateLimitExceeded) {
                        _state.update {
                            it.copy(
                                status = SearchStatus.Ready,
                                searchEnabled = true,
                                error = null
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            searchHistoryUseCase.getRecentSearches().collect { history ->
                _state.update { it.copy(searchHistory = history) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupQueryListener() {
        viewModelScope.launch {
            _state
                .debounce(300)
                .distinctUntilChanged { old, new -> old.query == new.query }
                .collect { state ->
                    if (state.query.isNotBlank() && state.query.length >= 2) {
                        updateSuggestions(state.query)
                    } else {
                        _state.update { it.copy(suggestedQueries = emptyList()) }
                    }
                }
        }
    }

    private suspend fun updateSuggestions(query: String) {
        val suggestions = searchHistoryUseCase.searchHistory(query)
        _state.update { it.copy(suggestedQueries = suggestions) }
    }

    fun onQueryChange(query: String) {
        _state.update {
            it.copy(
                query = query,
                status = when {
                    query.isEmpty() -> SearchStatus.Empty
                    else -> if (it.status == SearchStatus.RateLimitExceeded)
                        SearchStatus.RateLimitExceeded
                    else
                        SearchStatus.Ready
                },
                isShowingHistory = query.isEmpty()
            )
        }
    }

    fun onSearchTypeChange(type: SearchType) {
        _state.update {
            it.copy(
                searchType = type
            )
        }
        if (_state.value.query.isNotEmpty() && _state.value.searchEnabled) {
            search()
        }
    }

    fun onSearchFilterQualityAdded(quality: SearchQuality) {
        _state.update {
            it.apply { searchFilter.qualities.add(quality) }
        }
        onSearchFilterChanged()
    }

    fun onSearchFilterQualityRemoved(quality: SearchQuality) {
        _state.update {
            it.apply { searchFilter.qualities.remove(quality) }
        }
        onSearchFilterChanged()
    }

    fun onSearchContentFilterAdded(contentFilter: SearchContentFilter) {
        _state.update {
            it.apply { searchFilter.contentFilters.add(contentFilter) }
        }
        onSearchFilterChanged()
    }

    fun onSearchContentFilterRemoved(contentFilter: SearchContentFilter) {
        _state.update {
            it.apply { searchFilter.contentFilters.remove(contentFilter) }
        }
        onSearchFilterChanged()
    }

    private fun onSearchFilterChanged() {
        if (_state.value.results.isNotEmpty()) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        filteredResults = searchUseCase.filterSearchResults(
                            _state.value.results,
                            _state.value.searchFilter
                        )
                    )
                }
            }
        }
    }

    fun search() {
        val currentState = _state.value
        val query = currentState.query.trim()

        if (query.isBlank() || !currentState.searchEnabled) return

        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        status = SearchStatus.Loading,
                        isShowingHistory = false,
                        error = null
                    )
                }

                val serverUrl = settingsUseCase.getServerUrl()

                // Check if using example server before attempting network request
                if (serverUrl.contains("example.com")) {
                    _state.update {
                        it.copy(
                            error = context.getString(R.string.error_example_server),
                            status = SearchStatus.Error,
                            showServerRecommendation = true
                        )
                    }
                    return@launch
                }

                val results = when (currentState.searchType) {
                    SearchType.TRACKS -> searchUseCase.searchTracks(query)
                    SearchType.ALBUMS -> searchUseCase.searchAlbums(query)
                }

                searchHistoryUseCase.addSearch(query, currentState.searchType)

                _state.update {
                    it.copy(
                        results = results,
                        filteredResults = searchUseCase.filterSearchResults(
                            results,
                            _state.value.searchFilter
                        ),
                        status = if (results.isEmpty()) SearchStatus.NoResults else SearchStatus.Success,
                        showServerRecommendation = false
                    )
                }
            } catch (e: Exception) {
                handleSearchError(e)
            }
        }
    }

    private fun handleSearchError(e: Exception) {
        Log.e("SearchViewModel", "Search error", e)

        val isRateLimitError = e is CloudflareRateLimitException

        if (isRateLimitError) {
            viewModelScope.launch {
                searchRepository.setRateLimited(true)
            }

            _state.update {
                it.copy(
                    error = context.getString(R.string.cloudflare_rate_limit_message),
                    status = SearchStatus.RateLimitExceeded,
                    searchEnabled = false
                )
            }
        } else {
            viewModelScope.launch {
                try {
                    val serverUrl = settingsUseCase.getServerUrl()
                    val isExampleServer = serverUrl.contains("example.com") ||
                            e.message?.contains("example.com") == true

                    _state.update {
                        it.copy(
                            error = if (isExampleServer)
                                context.getString(R.string.error_example_server)
                            else
                                e.message,
                            status = SearchStatus.Error,
                            showServerRecommendation = isExampleServer
                        )
                    }
                } catch (ex: Exception) {
                    Log.e("SearchViewModel", "Error checking server URL", ex)
                    _state.update {
                        it.copy(
                            error = e.message,
                            status = SearchStatus.Error
                        )
                    }
                }
            }
        }
    }

    fun downloadTrack(track: SearchResult, config: QualityConfig) {
        viewModelScope.launch {
            processDownloadUseCase(
                DownloadRequest.Track(
                    track = track,
                    config = config
                )
            )
        }
    }

    fun clearSearch() {
        _state.update {
            it.copy(
                query = "",
                results = emptyList(),
                filteredResults = emptyList(),
                error = null,
                status = if (it.status == SearchStatus.RateLimitExceeded)
                    SearchStatus.RateLimitExceeded
                else
                    SearchStatus.Empty,
                showServerRecommendation = false,
                isShowingHistory = true
            )
        }
    }

    fun playTrackPreview(trackId: Long) {
        viewModelScope.launch {
            try {
                val preview = searchUseCase.getTrackPreview(trackId)
                if (preview.urls.isNotEmpty()) {
                    audioPreviewPlayer.play(preview.urls[0])
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error playing preview", e)
            }
        }
    }

    fun stopTrackPreview() {
        audioPreviewPlayer.stop()
    }

    fun useHistoryItem(item: SearchHistoryItem) {
        _state.update {
            it.copy(
                query = item.query,
                searchType = SearchType.valueOf(item.type)
            )
        }
        search()
    }

    fun deleteHistoryItem(item: SearchHistoryItem) {
        viewModelScope.launch {
            searchHistoryUseCase.deleteSearch(item)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryUseCase.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPreviewPlayer.stop()
    }
}