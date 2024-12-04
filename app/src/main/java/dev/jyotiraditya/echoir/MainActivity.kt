package dev.jyotiraditya.echoir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.jyotiraditya.echoir.data.permission.PermissionsManager
import dev.jyotiraditya.echoir.presentation.navigation.PredictiveBackHandler
import dev.jyotiraditya.echoir.presentation.screens.MainScreen
import dev.jyotiraditya.echoir.presentation.theme.EchoirTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var permissionsManager: PermissionsManager

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!permissionsManager.arePermissionsGranted()) {
            permissionsLauncher.launch(permissionsManager.requiredPermissions)
        }

        setContent {
            val navController = rememberNavController()
            val backHandler = remember { PredictiveBackHandler(this, navController) }

            EchoirTheme {
                backHandler.HandleBackPress {
                    MainScreen(navController = navController)
                }
            }
        }
    }
}