package app.echoirx.presentation.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.echoirx.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerBottomSheet(
    currentServer: String,
    onSave: (server: String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    var serverUrl by remember { mutableStateOf(currentServer) }
    var showError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.small,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.title_server_settings),
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = serverUrl,
                onValueChange = {
                    serverUrl = it
                    showError = false
                },
                label = { Text(stringResource(R.string.label_server_url)) },
                placeholder = { Text(stringResource(R.string.hint_server_url)) },
                singleLine = true,
                isError = showError,
                supportingText = {
                    if (showError) {
                        Text(
                            text = stringResource(R.string.error_empty_server_url),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (serverUrl.isNotBlank()) {
                            onSave(serverUrl)
                            onDismiss()
                        } else {
                            showError = true
                        }
                    }
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(onClick = {
                    serverUrl = "https://echoir.vercel.app/api"
                    showError = false
                    onReset()
                }) {
                    Text(stringResource(R.string.action_reset_to_default))
                }

                FilledTonalButton(
                    onClick = {
                        focusManager.clearFocus()
                        if (serverUrl.isNotBlank()) {
                            onSave(serverUrl)
                            onDismiss()
                        } else {
                            showError = true
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}