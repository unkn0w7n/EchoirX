package app.echoirx.data.utils.extensions

import org.json.JSONObject

fun String?.formatErrorMessage(defaultError: String): String = when {
    this == null -> defaultError
    startsWith("Illegal input") -> {
        substringAfter("JSON input: ")
            .let { jsonPart ->
                try {
                    val json = JSONObject(jsonPart)
                    json.toString(2)
                } catch (e: Exception) {
                    jsonPart
                }
            }
    }

    startsWith("{") -> {
        try {
            val json = JSONObject(this)
            json.toString(2)
        } catch (e: Exception) {
            this
        }
    }

    else -> this
}