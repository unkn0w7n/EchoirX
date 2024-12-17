package app.echoirx.domain.usecase

import app.echoirx.domain.model.Download
import app.echoirx.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDownloadsUseCase @Inject constructor(
    private val repository: DownloadRepository
) {
    fun getActiveDownloads(): Flow<List<Download>> =
        repository.getActiveDownloads()

    fun getDownloadHistory(): Flow<List<Download>> =
        repository.getDownloadHistory()
}