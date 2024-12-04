package dev.jyotiraditya.echoir.domain.model

enum class Region(val code: String, val displayName: String) {
    BRAZIL("BR", "Brazil"),
    FINLAND("FI", "Finland"),
    GERMANY("DE", "Germany"),
    MALAYSIA("MY", "Malaysia"),
    NEW_ZEALAND("NZ", "New Zealand"),
    UNITED_STATES("US", "United States");

    companion object {
        fun fromCode(code: String): Region = entries.find { it.code == code } ?: BRAZIL
    }
}