package app.echoirx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import app.echoirx.data.permission.PermissionsManager
import app.echoirx.presentation.screens.MainScreen
import app.echoirx.presentation.theme.EchoirTheme
import dagger.hilt.android.AndroidEntryPoint
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

            EchoirTheme {
                MainScreen(navController = navController)
            }
        }
    }
}