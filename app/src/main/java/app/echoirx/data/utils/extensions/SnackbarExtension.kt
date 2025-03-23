package app.echoirx.data.utils.extensions

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Extension function to show a snackbar message.
 *
 * @param message The text to display in the snackbar
 * @param scope CoroutineScope used to launch the snackbar
 * @param duration How long to display the message
 */
fun SnackbarHostState.showSnackbar(
    scope: CoroutineScope,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    scope.launch {
        showSnackbar(message = message, duration = duration)
    }
}