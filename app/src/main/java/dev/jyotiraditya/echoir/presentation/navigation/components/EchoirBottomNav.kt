package dev.jyotiraditya.echoir.presentation.navigation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.jyotiraditya.echoir.presentation.navigation.Route
import dev.jyotiraditya.echoir.presentation.navigation.navigationItems

@Composable
fun EchoirBottomNav(
    navController: NavHostController,
    currentRoute: Route?
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar {
        navigationItems.forEach { item ->
            val isSelected = when (item.route) {
                Route.Search.Main.path -> Route.Search.isInSearchSection(navBackStackEntry?.destination?.route)
                else -> currentRoute?.path == item.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = stringResource(item.label)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.label),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}