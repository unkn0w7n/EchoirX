package app.echoirx.data.remote.mapper

import app.echoirx.data.remote.dto.PlaybackResponseDto
import app.echoirx.domain.model.PlaybackResponse

object PlaybackMapper {
    fun PlaybackResponseDto.toDomain(): PlaybackResponse =
        PlaybackResponse(
            id = id,
            quality = quality,
            manifest = manifest,
            bitDepth = bitDepth,
            sampleRate = sampleRate,
            urls = urls,
            codec = codec
        )
}