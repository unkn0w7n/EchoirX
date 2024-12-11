package dev.jyotiraditya.echoir.domain.model

data class QueuedDownload(
    val download: Download,
    val config: QualityConfig
)