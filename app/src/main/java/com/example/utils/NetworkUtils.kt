package com.example.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

object NetworkUtils {

    fun normalizeUrl(rawUrl: String): String {
        var trimmed = rawUrl.trim()
        if (trimmed.isBlank()) return "https://"
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://$trimmed"
        }
        return trimmed
    }

    fun isValidUrl(url: String): Boolean {
        return try {
            val uri = URI(url)
            uri.scheme != null && (uri.scheme == "http" || uri.scheme == "https") && !uri.host.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }

    fun extractDomain(url: String): String {
        return try {
            val uri = URI(url)
            uri.host ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun suggestPackageName(domain: String, appName: String): String {
        val cleanDomain = domain.replace(Regex("^www\\."), "").split(".").reversed().filter { it.isNotBlank() }
        val prefix = if (cleanDomain.size >= 2) {
            "${cleanDomain[0]}.${cleanDomain[1]}"
        } else if (cleanDomain.isNotEmpty()) {
            "com.${cleanDomain[0]}"
        } else {
            "com.example"
        }
        val cleanAppName = appName.replace(Regex("[^a-zA-Z0-9]"), "").lowercase().ifBlank { "app" }
        return "$prefix.$cleanAppName".lowercase().replace(Regex("[^a-z0-9.]"), "")
    }

    suspend fun fetchFavicon(targetUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        val domain = extractDomain(targetUrl)
        if (domain.isBlank()) return@withContext null

        // Try Google Favicon Service
        val googleFaviconUrl = "https://www.google.com/s2/favicons?domain=$domain&sz=128"
        try {
            val url = URL(googleFaviconUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 4000
            connection.readTimeout = 4000
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { input ->
                    return@withContext BitmapFactory.decodeStream(input)
                }
            }
        } catch (e: Exception) {
            // ignore
        }
        null
    }
}
