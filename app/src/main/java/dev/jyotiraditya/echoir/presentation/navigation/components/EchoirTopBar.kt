package dev.jyotiraditya.echoir.presentation.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import dev.jyotiraditya.echoir.presentation.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoirTopBar(
    currentRoute: Route?,
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = when (currentRoute) {
                    is Route.Home -> "Home"
                    is Route.Settings -> "Settings"
                    is Route.Search.Main -> "Search"
                    is Route.Search.Details -> "Details"
                    null -> ""
                },
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (currentRoute is Route.Search.Details) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
    )
}