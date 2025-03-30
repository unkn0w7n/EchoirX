package app.echoirx.presentation.screens.settings.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.echoirx.R
import app.echoirx.presentation.components.preferences.PreferenceItem
import app.echoirx.presentation.components.preferences.PreferencePosition
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun WorkerLimitsInfo(
    position: PreferencePosition = PreferencePosition.Single
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    val utcDate = Date(currentTime)
    val formattedDate = dateFormat.format(utcDate)
    val formattedTime = timeFormat.format(utcDate)

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.time = utcDate
    val currentSeconds = calendar.get(Calendar.HOUR_OF_DAY) * 3600 +
            calendar.get(Calendar.MINUTE) * 60 +
            calendar.get(Calendar.SECOND)
    val secondsInDay = 24 * 3600
    val secondsUntilMidnight = secondsInDay - currentSeconds

    val hoursUntilMidnight = secondsUntilMidnight / 3600
    val minutesUntilMidnight = (secondsUntilMidnight % 3600) / 60
    val secondsRemaining = secondsUntilMidnight % 60

    val currentUtc = stringResource(
        R.string.label_current_utc,
        formattedDate,
        formattedTime
    )
    val timeUntilReset = stringResource(
        R.string.label_time_until_reset,
        hoursUntilMidnight,
        minutesUntilMidnight,
        secondsRemaining
    )

    PreferenceItem(
        title = currentUtc,
        subtitle = timeUntilReset,
        icon = Icons.Outlined.AccessTime,
        position = position
    )
}