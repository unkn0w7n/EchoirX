package dev.jyotiraditya.echoir.presentation.screens.settings

import dev.jyotiraditya.echoir.domain.model.FileNamingFormat

data class SettingsState(
    val outputDirectory: String? = null,
    val fileNamingFormat: FileNamingFormat = FileNamingFormat.TITLE_ONLY,
    val region: String = "BR"
)