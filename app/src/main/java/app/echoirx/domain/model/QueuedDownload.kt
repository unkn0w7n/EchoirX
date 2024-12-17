package app.echoirx.domain.model

data class QueuedDownload(
    val download: Download,
    val config: QualityConfig
)