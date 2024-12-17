package app.echoirx.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.echoirx.R

data class NavigationItem(
    val route: String,
    @DrawableRes val outlinedIcon: Int,
    @DrawableRes val filledIcon: Int,
    @StringRes val label: Int
)

val navigationItems = listOf(
    NavigationItem(
        route = Route.Home.path,
        outlinedIcon = R.drawable.ic_home,
        filledIcon = R.drawable.ic_home_filled,
        label = R.string.nav_home
    ),
    NavigationItem(
        route = Route.Search.Main.path,
        outlinedIcon = R.drawable.ic_search,
        filledIcon = R.drawable.ic_search_filled,
        label = R.string.nav_search
    ),
    NavigationItem(
        route = Route.Settings.path,
        outlinedIcon = R.drawable.ic_settings,
        filledIcon = R.drawable.ic_settings_filled,
        label = R.string.nav_settings
    )
)