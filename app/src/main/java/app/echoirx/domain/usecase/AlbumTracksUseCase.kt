package app.echoirx.domain.usecase

import app.echoirx.domain.model.SearchResult
import app.echoirx.domain.repository.SearchRepository
import javax.inject.Inject

class AlbumTracksUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(albumId: Long): List<SearchResult> =
        repository.getAlbumTracks(albumId)
}