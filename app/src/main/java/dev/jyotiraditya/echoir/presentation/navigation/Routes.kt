package dev.jyotiraditya.echoir.presentation.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Settings : Route("settings")

    sealed class Search(subPath: String = "") : Route("search$subPath") {
        data object Main : Search()
        data class Details(val type: String = "{type}", val id: Long = -1) :
            Search("/details/{type}/{id}") {
            fun createRoute() = "search/details/{type}/{id}"
            fun createPath(type: String, id: Long) = "search/details/$type/$id"

            companion object {
                const val TYPE_ARG = "type"
                const val ID_ARG = "id"
            }
        }

        companion object {
            fun isInSearchSection(route: String?) =
                route?.startsWith("search") == true
        }
    }

    companion object {
        fun fromPath(route: String?): Route? = when {
            route == Home.path -> Home
            route == Settings.path -> Settings
            route == Search.Main.path -> Search.Main
            route?.startsWith("search/details/") == true -> {
                val parts = route.split("/")
                if (parts.size >= 4) {
                    Search.Details(parts[2], parts[3].toLongOrNull() ?: -1)
                } else null
            }

            else -> null
        }
    }
}