package app.echoirx.domain.model

data class AlbumDownloadContext(
    val id: Long,
    val title: String,
    val directory: String,
    val isExplicit: Boolean
)

sealed class DownloadRequest {
    data class Track(
        val track: SearchResult,
        val config: QualityConfig
    ) : DownloadRequest()

    data class Album(
        val album: SearchResult,
        val tracks: List<SearchResult>,
        val config: QualityConfig,
        val downloadContext: AlbumDownloadContext
    ) : DownloadRequest()
}