package app.echoirx.data.remote.api

import android.content.Context
import app.echoirx.R

/**
 * Exception thrown when Cloudflare rate limit is exceeded (429 Too Many Requests)
 */
class CloudflareRateLimitException(message: String) : Exception(message) {
    companion object {
        fun create(context: Context): CloudflareRateLimitException {
            return CloudflareRateLimitException(
                context.getString(R.string.cloudflare_rate_limit_exception)
            )
        }
    }
}