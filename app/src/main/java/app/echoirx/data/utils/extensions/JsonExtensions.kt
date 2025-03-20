package app.echoirx.data.utils.extensions

import org.json.JSONException
import org.json.JSONObject

fun String?.formatErrorMessage(defaultError: String): String = when {
    this == null -> defaultError

    startsWith("Illegal input") -> {
        substringAfter("JSON input: ")
            .let { jsonPart ->
                extractMessageFromJson(jsonPart) ?: jsonPart
            }
    }

    startsWith("{") -> {
        extractMessageFromJson(this) ?: this
    }

    else -> this
}

private fun extractMessageFromJson(jsonString: String): String? {
    return try {
        val json = JSONObject(jsonString)

        if (json.has("message") && !json.isNull("message")) {
            return json.getString("message")
        }

        json.keys().forEach { key ->
            try {
                val nestedObj = json.optJSONObject(key)
                if (nestedObj != null && nestedObj.has("message") && !nestedObj.isNull("message")) {
                    return nestedObj.getString("message")
                }
            } catch (_: JSONException) {
            }
        }

        return json.toString(2)
    } catch (_: Exception) {
        null
    }
}