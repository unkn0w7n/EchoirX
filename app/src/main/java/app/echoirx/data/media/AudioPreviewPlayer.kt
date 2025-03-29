package app.echoirx.data.media

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPreviewPlayer @Inject constructor() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingUrl: String? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    suspend fun play(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (url == currentlyPlayingUrl) {
                return@withContext togglePlayback()
            }

            releaseMediaPlayer()

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlaying.value = true
                }
                setOnCompletionListener { _ ->
                    _isPlaying.value = false
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPreviewPlayer", "Error: $what, extra: $extra")
                    _isPlaying.value = false
                    true
                }
                prepareAsync()
            }

            currentlyPlayingUrl = url
            true
        } catch (e: Exception) {
            Log.e("AudioPreviewPlayer", "Error playing audio", e)
            false
        }
    }

    fun stop() {
        releaseMediaPlayer()
        _isPlaying.value = false
        currentlyPlayingUrl = null
    }

    private fun togglePlayback(): Boolean {
        return try {
            if (_isPlaying.value) {
                mediaPlayer?.pause()
                _isPlaying.value = false
            } else {
                mediaPlayer?.start()
                _isPlaying.value = true
            }
            true
        } catch (e: Exception) {
            Log.e("AudioPreviewPlayer", "Error toggling playback", e)
            false
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}