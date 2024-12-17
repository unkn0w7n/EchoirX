package app.echoirx.presentation.navigation

import android.os.Build
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavHostController

class PredictiveBackHandler(
    private val activity: ComponentActivity,
    private val navController: NavHostController
) {
    private var isEnabled by mutableStateOf(false)
    private var backCallback: OnBackInvokedCallback? = null

    private fun enablePredictiveBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backCallback = OnBackInvokedCallback {
                when {
                    navController.previousBackStackEntry != null -> {
                        navController.popBackStack()
                    }

                    else -> {
                        activity.finish()
                    }
                }
            }
            activity.onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                backCallback!!
            )
        }
        isEnabled = true
    }

    private fun disablePredictiveBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backCallback?.let { callback ->
                activity.onBackInvokedDispatcher.unregisterOnBackInvokedCallback(callback)
            }
            backCallback = null
        }
        isEnabled = false
    }

    @Composable
    fun HandleBackPress(
        content: @Composable () -> Unit
    ) {
        val lifecycleState by activity.lifecycle.currentStateAsState()

        DisposableEffect(lifecycleState) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    enablePredictiveBack()
                }
            }

            activity.lifecycle.addObserver(observer)

            onDispose {
                activity.lifecycle.removeObserver(observer)
                disablePredictiveBack()
            }
        }

        content()
    }
}