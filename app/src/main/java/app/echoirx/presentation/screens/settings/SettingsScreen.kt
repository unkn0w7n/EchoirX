package app.echoirx.presentation.screens.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.echoirx.BuildConfig
import app.echoirx.R
import app.echoirx.data.utils.extensions.toDisplayPath
import app.echoirx.domain.model.Region
import app.echoirx.presentation.components.preferences.PreferenceCategory
import app.echoirx.presentation.components.preferences.PreferenceItem
import app.echoirx.presentation.components.preferences.PreferencePosition
import app.echoirx.presentation.screens.settings.components.CrucialSettingsDialog
import app.echoirx.presentation.screens.settings.components.FileNamingFormatDialog
import app.echoirx.presentation.screens.settings.components.RegionDialog
import app.echoirx.presentation.screens.settings.components.ServerBottomSheet
import androidx.core.net.toUri

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsState()

    var showFormatDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showRegionDialog by remember { mutableStateOf(false) }
    var showServerSheet by remember { mutableStateOf(false) }

    val dirPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.updateOutputDirectory(it.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dirPicker.launch(null)
        }
    }

    if (showFormatDialog) {
        FileNamingFormatDialog(
            selectedFormat = state.fileNamingFormat,
            onSelectFormat = { format ->
                viewModel.updateFileNamingFormat(format)
                showFormatDialog = false
            },
            onDismiss = { showFormatDialog = false }
        )
    }

    if (showResetDialog) {
        CrucialSettingsDialog(
            onDismiss = { showResetDialog = false },
            icon = Icons.Outlined.RestartAlt,
            title = stringResource(R.string.dialog_reset_settings_title),
            description = stringResource(R.string.dialog_reset_settings_message),
            confirmText = stringResource(R.string.action_reset),
            onConfirm = {
                viewModel.resetSettings()
                showResetDialog = false
            }
        )
    }

    if (showClearDataDialog) {
        CrucialSettingsDialog(
            onDismiss = { showClearDataDialog = false },
            icon = Icons.Outlined.Delete,
            title = stringResource(R.string.dialog_clear_data_title),
            description = stringResource(R.string.dialog_clear_data_message),
            confirmText = stringResource(R.string.action_clear),
            onConfirm = {
                viewModel.clearData()
                showClearDataDialog = false
            }
        )
    }

    if (showRegionDialog) {
        RegionDialog(
            selectedRegion = state.region,
            onSelectRegion = { region ->
                viewModel.updateRegion(region)
                showRegionDialog = false
            },
            onDismiss = { showRegionDialog = false }
        )
    }

    if (showServerSheet) {
        ServerBottomSheet(
            currentServer = state.serverUrl,
            onSave = { serverUrl ->
                viewModel.updateServerUrl(serverUrl)
            },
            onReset = {
                viewModel.resetServerSettings()
            },
            onDismiss = { showServerSheet = false },
            focusManager = focusManager
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            PreferenceCategory(title = stringResource(R.string.title_content))
        }

        item {
            val regionName = Region.getDisplayName(Region.fromCode(state.region), context)
            PreferenceItem(
                title = stringResource(R.string.title_region),
                subtitle = "$regionName - ${state.region}",
                icon = Icons.Outlined.Public,
                onClick = { showRegionDialog = true },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_server),
                subtitle = stringResource(R.string.msg_server_subtitle),
                icon = Icons.Outlined.CloudQueue,
                onClick = { showServerSheet = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = stringResource(R.string.title_storage))
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_download_location),
                subtitle = state.outputDirectory.toDisplayPath(context),
                icon = Icons.Outlined.Folder,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        dirPicker.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_file_naming_format),
                subtitle = stringResource(state.fileNamingFormat.displayNameResId),
                icon = Icons.Outlined.TextFormat,
                onClick = { showFormatDialog = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = stringResource(R.string.title_data))
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_data),
                subtitle = stringResource(R.string.msg_clear_data_subtitle),
                icon = Icons.Outlined.Delete,
                onClick = { showClearDataDialog = true },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.dialog_reset_settings_title),
                subtitle = stringResource(R.string.msg_reset_settings_subtitle),
                icon = Icons.Outlined.RestartAlt,
                onClick = { showResetDialog = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = stringResource(R.string.title_about))
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.msg_about_version, BuildConfig.VERSION_NAME),
                icon = Icons.Outlined.Info,
                position = PreferencePosition.Top,
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_ladybug),
                subtitle = stringResource(R.string.msg_ladybug_subtitle),
                icon = painterResource(R.drawable.ic_handshake),
                position = PreferencePosition.Middle
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_telegram_channel),
                subtitle = stringResource(R.string.url_telegram),
                icon = Icons.Outlined.Public,
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.url_telegram_full))
                    )
                    context.startActivity(intent)
                },
                position = PreferencePosition.Middle
            )
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.title_donate),
                subtitle = stringResource(R.string.msg_donate_subtitle),
                icon = Icons.Outlined.LocalCafe,
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.url_donate))
                    )
                    context.startActivity(intent)
                },
                position = PreferencePosition.Bottom
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}