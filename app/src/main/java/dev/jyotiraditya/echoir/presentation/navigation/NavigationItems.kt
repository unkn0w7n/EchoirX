package dev.jyotiraditya.echoir.presentation.navigation

import dev.jyotiraditya.echoir.R

data class NavigationItem(
    val route: String,
    val icon: Int,
    val label: String
)

val navigationItems = listOf(
    NavigationItem(
        route = Route.Home.path,
        icon = R.drawable.ic_home,
        label = "Home"
    ),
    NavigationItem(
        route = Route.Search.Main.path,
        icon = R.drawable.ic_search,
        label = "Search"
    ),
    NavigationItem(
        route = Route.Settings.path,
        icon = R.drawable.ic_settings,
        label = "Settings"
    )
)