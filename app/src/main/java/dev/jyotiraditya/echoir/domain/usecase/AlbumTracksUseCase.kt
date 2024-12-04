package dev.jyotiraditya.echoir.domain.usecase

import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.repository.SearchRepository
import javax.inject.Inject

class AlbumTracksUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(albumId: Long): List<SearchResult> =
        repository.getAlbumTracks(albumId)
}