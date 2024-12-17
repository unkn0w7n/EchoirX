package app.echoirx.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.echoirx.domain.usecase.GetDownloadsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDownloadsUseCase: GetDownloadsUseCase
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
}