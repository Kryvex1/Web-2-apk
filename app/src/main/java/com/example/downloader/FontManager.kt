package com.example.downloader

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Automated Font Downloader & Local Cache Engine for Poppins Family.
 * Stores TTF files in `context.filesDir/fonts/` and builds dynamic Compose FontFamilies.
 */
object FontManager {
    private const val TAG = "FontManager"

    // Google Fonts CDN static TTF links for Poppins variants
    private val FONT_URLS = mapOf(
        "Poppins-Light.ttf" to Pair(
            "https://raw.githubusercontent.com/google/fonts/main/ofl/poppins/Poppins-Light.ttf",
            FontWeight.Light
        ),
        "Poppins-Regular.ttf" to Pair(
            "https://raw.githubusercontent.com/google/fonts/main/ofl/poppins/Poppins-Regular.ttf",
            FontWeight.Normal
        ),
        "Poppins-Medium.ttf" to Pair(
            "https://raw.githubusercontent.com/google/fonts/main/ofl/poppins/Poppins-Medium.ttf",
            FontWeight.Medium
        ),
        "Poppins-SemiBold.ttf" to Pair(
            "https://raw.githubusercontent.com/google/fonts/main/ofl/poppins/Poppins-SemiBold.ttf",
            FontWeight.SemiBold
        ),
        "Poppins-Bold.ttf" to Pair(
            "https://raw.githubusercontent.com/google/fonts/main/ofl/poppins/Poppins-Bold.ttf",
            FontWeight.Bold
        )
    )

    private val _fontFamilyState = mutableStateOf<FontFamily>(FontFamily.Default)
    val fontFamilyState: State<FontFamily> = _fontFamilyState

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    private val _cachedFontsCount = MutableStateFlow(0)
    val cachedFontsCount: StateFlow<Int> = _cachedFontsCount.asStateFlow()

    fun initialize(context: Context, scope: CoroutineScope) {
        scope.launch {
            loadOrDownloadFonts(context.applicationContext)
        }
    }

    private suspend fun loadOrDownloadFonts(context: Context) = withContext(Dispatchers.IO) {
        val fontsDir = File(context.filesDir, "fonts")
        if (!fontsDir.exists()) {
            fontsDir.mkdirs()
        }

        // Check already downloaded fonts
        var readyFonts = 0
        FONT_URLS.keys.forEach { fileName ->
            val file = File(fontsDir, fileName)
            if (file.exists() && file.length() > 1024) {
                readyFonts++
            }
        }
        _cachedFontsCount.value = readyFonts

        if (readyFonts >= FONT_URLS.size) {
            // All fonts are locally cached
            buildFontFamilyFromCache(fontsDir)
            return@withContext
        }

        // Download missing fonts asynchronously
        _isDownloading.value = true
        var completed = readyFonts

        FONT_URLS.forEach { (fileName, pair) ->
            val targetFile = File(fontsDir, fileName)
            if (!targetFile.exists() || targetFile.length() < 1024) {
                val success = downloadFontFile(pair.first, targetFile)
                if (success) {
                    completed++
                    _cachedFontsCount.value = completed
                    _downloadProgress.value = completed.toFloat() / FONT_URLS.size.toFloat()
                }
            }
        }

        _isDownloading.value = false
        buildFontFamilyFromCache(fontsDir)
    }

    private fun downloadFontFile(urlStr: String, destination: File): Boolean {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.instanceFollowRedirects = true

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { input ->
                    FileOutputStream(destination).use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } else {
                Log.w(TAG, "Failed downloading $urlStr HTTP ${connection.responseCode}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading font from $urlStr: ${e.message}")
            false
        }
    }

    private fun buildFontFamilyFromCache(fontsDir: File) {
        try {
            val fontList = mutableListOf<Font>()
            FONT_URLS.forEach { (fileName, pair) ->
                val file = File(fontsDir, fileName)
                if (file.exists() && file.length() > 1024) {
                    try {
                        fontList.add(Font(file = file, weight = pair.second))
                    } catch (e: Exception) {
                        Log.w(TAG, "Could not load font $fileName: ${e.message}")
                    }
                }
            }

            if (fontList.isNotEmpty()) {
                _fontFamilyState.value = FontFamily(fontList)
                Log.d(TAG, "Loaded ${fontList.size} custom Poppins fonts successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error building custom font family: ${e.message}")
        }
    }
}
