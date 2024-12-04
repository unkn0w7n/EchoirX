package dev.jyotiraditya.echoir.data.utils.extensions

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import java.io.File

/**
 * Extension functions for handling storage paths and URIs
 */
fun String?.toDisplayPath(context: Context): String {
    return when {
        this == null -> {
            val musicDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            File(musicDir, "Echoir").path.formatLocalPath()
        }

        Uri.parse(this).scheme == "content" -> {
            val uri = Uri.parse(this)

            val treePath = uri.pathSegments
                .firstOrNull { it.contains(":") }
                ?.substringAfter(":")
                ?.replace("/", File.separator)

            if (treePath != null) {
                if (uri.toString().contains("primary")) {
                    "Internal Storage/$treePath"
                } else {
                    "External Storage/$treePath"
                }
            } else {
                val doc = DocumentFile.fromTreeUri(context, uri)
                if (doc != null) {
                    if (uri.toString().contains("primary")) {
                        "Internal Storage/${doc.name}"
                    } else {
                        "External Storage/${doc.name}"
                    }
                } else {
                    "Internal Storage"
                }
            }
        }

        else -> Uri.parse(this).path ?: this
    }.formatLocalPath()
}

/**
 * Formats a local file system path for better readability
 */
private fun String.formatLocalPath(): String {
    return this.replace("/storage/emulated/0/", "Internal Storage/")
        .replace("//", "/")
        .trim('/')
}