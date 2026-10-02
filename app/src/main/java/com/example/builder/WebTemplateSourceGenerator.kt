package com.example.builder

import com.example.data.model.ScreenMode
import com.example.data.model.SplashStyle
import com.example.data.model.WebProjectConfig

object WebTemplateSourceGenerator {

    fun generateMainActivityKt(config: WebProjectConfig): String {
        val packagePath = config.packageName

        return """
package $packagePath

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var splashContainer: FrameLayout
    private lateinit var progressBar: ProgressBar
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        applyScreenMode()
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        splashContainer = findViewById(R.id.splashContainer)
        progressBar = findViewById(R.id.progressBar)

        setupWebView()
        setupBackHandler()

        // Load configured Target URL
        webView.loadUrl("${config.targetUrl}")

        // Dismiss splash screen smoothly after duration
        Handler(Looper.getMainLooper()).postDelayed({
            splashContainer.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction { splashContainer.visibility = View.GONE }
                .start()
        }, ${config.splashDurationMs}L)
    }

    private fun applyScreenMode() {
        ${
            when (config.screenMode) {
                ScreenMode.PURE_FULLSCREEN -> """
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )
                """.trimIndent()
                ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR -> """
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }
                """.trimIndent()
                ScreenMode.CLASSIC_APP_BAR -> """
        supportActionBar?.title = "${config.appName}"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                """.trimIndent()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = ${config.enableJsInterface}
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.allowFileAccess = ${config.enableFileUpload}
        settings.allowContentAccess = true
        settings.setSupportZoom(${config.enableZoomControls})
        settings.builtInZoomControls = ${config.enableZoomControls}
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.mediaPlaybackRequiresUserGesture = false

        ${
            if (config.enableDesktopUserAgent) {
                """settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36""""
            } else if (config.customUserAgent.isNotBlank()) {
                """settings.userAgentString = "${config.customUserAgent}""""
            } else {
                ""
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                val allowedDomains = listOf(${config.whitelistedDomains.joinToString(",") { "\"$it\"" }})
                
                if (allowedDomains.isEmpty()) {
                    return false
                }
                
                val isAllowed = allowedDomains.any { url.contains(it) }
                return if (!isAllowed) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        true
                    } catch (e: Exception) {
                        false
                    }
                } else {
                    false
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress >= 100) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.filePathCallback = filePathCallback
                val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "*/*"
                }
                try {
                    startActivityForResult(intent, 1001)
                } catch (e: Exception) {
                    this@MainActivity.filePathCallback = null
                    return false
                }
                return true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                val results = if (data?.data != null) {
                    arrayOf(data.data!!)
                } else {
                    null
                }
                filePathCallback?.onReceiveValue(results)
            } else {
                filePathCallback?.onReceiveValue(null)
            }
            filePathCallback = null
        }
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}
        """.trimIndent()
    }

    fun generateActivityMainXml(config: WebProjectConfig): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="${config.splashBgColorHex}">

    <WebView
        android:id="@+id/webView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <ProgressBar
        android:id="@+id/progressBar"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="4dp"
        android:layout_gravity="top"
        android:indeterminate="false"
        android:max="100"
        android:progressDrawable="@drawable/custom_progress"
        android:visibility="gone" />

    <FrameLayout
        android:id="@+id/splashContainer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="${config.splashBgColorHex}">

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:gravity="center"
            android:orientation="vertical">

            <ImageView
                android:id="@+id/splashLogo"
                android:layout_width="96dp"
                android:layout_height="96dp"
                android:src="@mipmap/ic_launcher" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:text="${config.appName}"
                android:textColor="#FFFFFF"
                android:textSize="20sp"
                android:textStyle="bold" />
        </LinearLayout>
    </FrameLayout>
</FrameLayout>
        """.trimIndent()
    }

    fun generateStringsXml(config: WebProjectConfig): String {
        return """<resources>
    <string name="app_name">${config.appName}</string>
</resources>"""
    }

    fun generateColorsXml(config: WebProjectConfig): String {
        return """<resources>
    <color name="primary">${config.primaryColorHex}</color>
    <color name="splash_bg">${config.splashBgColorHex}</color>
</resources>"""
    }

    fun generateGradleKts(config: WebProjectConfig): String {
        return """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${config.packageName}"
    compileSdk = 35

    defaultConfig {
        applicationId = "${config.packageName}"
        minSdk = 26
        targetSdk = 35
        versionCode = ${config.versionCode}
        versionName = "${config.versionName}"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
        """.trimIndent()
    }
}
