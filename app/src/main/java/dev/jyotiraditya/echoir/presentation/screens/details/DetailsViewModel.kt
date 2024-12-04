package dev.jyotiraditya.echoir.presentation.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jyotiraditya.echoir.domain.model.AlbumDownloadContext
import dev.jyotiraditya.echoir.domain.model.DownloadRequest
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import dev.jyotiraditya.echoir.domain.usecase.AlbumTracksUseCase
import dev.jyotiraditya.echoir.domain.usecase.ProcessDownloadUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getAlbumTracksUseCase: AlbumTracksUseCase,
    private val processDownloadUseCase: ProcessDownloadUseCase,
    private val downloadRepository: DownloadRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    fun initializeWithItem(item: SearchResult) {
        _state.update { it.copy(item = item) }
        loadAlbumTracks(item.id)
    }

    fun downloadTrack(track: SearchResult, config: QualityConfig) {
        viewModelScope.launch {
            try {
                processDownloadUseCase(
                    DownloadRequest.Track(
                        track = track,
                        config = config
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun downloadAlbum(config: QualityConfig) {
        viewModelScope.launch {
            try {
                val album = state.value.item ?: return@launch
                val tracks = state.value.tracks
                val isExplicit = tracks.any { it.explicit }

                // Create album directory first
                val albumDirectory =
                    downloadRepository.createAlbumDirectory(album.title, isExplicit)

                val downloadContext = AlbumDownloadContext(
                    id = album.id,
                    title = album.title,
                    directory = albumDirectory,
                    isExplicit = isExplicit
                )

                processDownloadUseCase(
                    DownloadRequest.Album(
                        album = album,
                        tracks = tracks,
                        config = config,
                        downloadContext = downloadContext
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun loadAlbumTracks(albumId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val tracks = getAlbumTracksUseCase(albumId)
                _state.update { it.copy(tracks = tracks, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}