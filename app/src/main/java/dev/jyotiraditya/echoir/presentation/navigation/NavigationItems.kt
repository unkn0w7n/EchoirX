package dev.jyotiraditya.echoir.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.jyotiraditya.echoir.R

data class NavigationItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
)

val navigationItems = listOf(
    NavigationItem(
        route = Route.Home.path,
        icon = R.drawable.ic_home,
        label = R.string.nav_home
    ),
    NavigationItem(
        route = Route.Search.Main.path,
        icon = R.drawable.ic_search,
        label = R.string.nav_search
    ),
    NavigationItem(
        route = Route.Settings.path,
        icon = R.drawable.ic_settings,
        label = R.string.nav_settings
    )
)