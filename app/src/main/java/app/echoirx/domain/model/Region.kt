package app.echoirx.domain.model

import android.content.Context
import androidx.annotation.ArrayRes
import app.echoirx.R

enum class Region(val code: String) {
    ALBANIA("AL"),
    ARGENTINA("AR"),
    AUSTRALIA("AU"),
    AUSTRIA("AT"),
    BELGIUM("BE"),
    BRAZIL("BR"),
    CANADA("CA"),
    CHILE("CL"),
    COLOMBIA("CO"),
    DOMINICAN_REPUBLIC("DO"),
    FRANCE("FR"),
    GERMANY("DE"),
    HONG_KONG("HK"),
    ISRAEL("IL"),
    ITALY("IT"),
    JAMAICA("JM"),
    MALAYSIA("MY"),
    MEXICO("MX"),
    NEW_ZEALAND("NZ"),
    NIGERIA("NG"),
    PERU("PE"),
    PUERTO_RICO("PR"),
    SINGAPORE("SG"),
    SOUTH_AFRICA("ZA"),
    SPAIN("ES"),
    THAILAND("TH"),
    UGANDA("UG"),
    UNITED_ARAB_EMIRATES("AE"),
    UNITED_KINGDOM("GB"),
    UNITED_STATES("US");

    companion object {
        @ArrayRes
        private val COUNTRY_NAMES = R.array.region_country_names

        fun fromCode(code: String): Region = entries.find { it.code == code } ?: BRAZIL

        fun getDisplayName(region: Region, context: Context): String =
            context.resources.getStringArray(COUNTRY_NAMES)[region.ordinal]
    }
}