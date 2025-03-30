package app.echoirx.presentation.components.models

import androidx.compose.ui.graphics.vector.ImageVector

data class ChipAction(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)