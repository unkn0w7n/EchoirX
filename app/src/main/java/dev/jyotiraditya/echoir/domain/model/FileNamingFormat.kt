package dev.jyotiraditya.echoir.domain.model

import androidx.annotation.StringRes
import dev.jyotiraditya.echoir.R

enum class FileNamingFormat(
    @StringRes val displayNameResId: Int,
    val format: (String, String) -> String
) {
    ARTIST_TITLE(
        R.string.file_format_artist_title_display,
        { artist, title -> "${artist.split(",").first().trim()} - $title" }
    ),
    TITLE_ARTIST(
        R.string.file_format_title_artist_display,
        { artist, title -> "$title - ${artist.split(",").first().trim()}" }
    ),
    TITLE_ONLY(
        R.string.file_format_title_only_display,
        { _, title -> title }
    );

    companion object {
        fun fromOrdinal(ordinal: Int) = entries.getOrNull(ordinal) ?: TITLE_ONLY
    }
}