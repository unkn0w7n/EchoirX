package dev.jyotiraditya.echoir.domain.model

enum class FileNamingFormat(
    val displayName: String,
    val format: (String, String) -> String
) {
    ARTIST_TITLE(
        "Artist - Title",
        { artist, title -> "${artist.split(",").first().trim()} - $title" }),
    TITLE_ARTIST(
        "Title - Artist",
        { artist, title -> "$title - ${artist.split(",").first().trim()}" }),
    TITLE_ONLY("Title", { _, title -> title });

    companion object {
        fun fromOrdinal(ordinal: Int) = entries.getOrNull(ordinal) ?: TITLE_ONLY
    }
}