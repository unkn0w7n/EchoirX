package app.echoirx.presentation.screens.settings

import app.echoirx.domain.model.FileNamingFormat

data class SettingsState(
    val outputDirectory: String? = null,
    val fileNamingFormat: FileNamingFormat = FileNamingFormat.TITLE_ONLY,
    val region: String = "BR"
)