package dev.jyotiraditya.echoir.domain.model

enum class Region(val code: String, val displayName: String) {
    ALBANIA("AL", "Albania"),
    ARGENTINA("AR", "Argentina"),
    AUSTRALIA("AU", "Australia"),
    AUSTRIA("AT", "Austria"),
    BELGIUM("BE", "Belgium"),
    BRAZIL("BR", "Brazil"),
    CANADA("CA", "Canada"),
    CHILE("CL", "Chile"),
    COLOMBIA("CO", "Colombia"),
    DOMINICAN_REPUBLIC("DO", "Dominican Republic"),
    FRANCE("FR", "France"),
    GERMANY("DE", "Germany"),
    HONG_KONG("HK", "Hong Kong"),
    ISRAEL("IL", "Israel"),
    ITALY("IT", "Italy"),
    JAMAICA("JM", "Jamaica"),
    MALAYSIA("MY", "Malaysia"),
    MEXICO("MX", "Mexico"),
    NEW_ZEALAND("NZ", "New Zealand"),
    NIGERIA("NG", "Nigeria"),
    PERU("PE", "Peru"),
    PUERTO_RICO("PR", "Puerto Rico"),
    SINGAPORE("SG", "Singapore"),
    SOUTH_AFRICA("ZA", "South Africa"),
    SPAIN("ES", "Spain"),
    THAILAND("TH", "Thailand"),
    UGANDA("UG", "Uganda"),
    UNITED_ARAB_EMIRATES("AE", "United Arab Emirates"),
    UNITED_KINGDOM("GB", "United Kingdom"),
    UNITED_STATES("US", "United States");

    companion object {
        fun fromCode(code: String): Region = entries.find { it.code == code } ?: BRAZIL
    }
}