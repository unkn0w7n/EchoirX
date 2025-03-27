package app.echoirx.presentation.screens.home

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.echoirx.domain.model.Download
import app.echoirx.domain.repository.DownloadRepository
import app.echoirx.domain.usecase.GetDownloadsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDownloadsUseCase: GetDownloadsUseCase,
    private val downloadRepository: DownloadRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getDownloadsUseCase.getActiveDownloads()
                .combine(getDownloadsUseCase.getDownloadHistory()) { active, history ->
                    HomeState(
                        activeDownloads = active,
                        downloadHistory = history,
                        isLoading = false
                    )
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            error = error.message,
                            isLoading = false
                        )
                    }
                }
                .collect { newState ->
                    _state.update { newState }
                }
        }
    }

    fun deleteDownload(download: Download): Boolean {
        return try {
            viewModelScope.launch {
                downloadRepository.deleteDownload(download)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun deleteFile(download: Download): Boolean {
        if (download.filePath.isNullOrEmpty()) return false

        return try {
            val path = download.filePath
            if (path.startsWith("content://")) {
                val uri = path.toUri()
                context.contentResolver.delete(uri, null, null)
            } else {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun shareFile(download: Download): Intent? {
        if (download.filePath.isNullOrEmpty()) return null

        return try {
            val uri = if (download.filePath.startsWith("content://")) {
                download.filePath.toUri()
            } else {
                val file = File(download.filePath)
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
            }

            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "audio/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (_: Exception) {
            null
        }
    }
}