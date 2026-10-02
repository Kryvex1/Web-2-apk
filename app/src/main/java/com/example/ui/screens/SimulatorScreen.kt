package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ScreenMode
import com.example.data.model.WebProjectConfig
import com.example.ui.components.LiveSplashPreview
import com.example.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreen(
    config: WebProjectConfig,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf(config.appName) }
    var currentUrl by remember { mutableStateOf(config.targetUrl) }
    var pageProgress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var showSplash by remember { mutableStateOf(true) }
    var isDesktopMode by remember { mutableStateOf(config.enableDesktopUserAgent) }

    // Screen mode override for testing
    var activeScreenMode by remember { mutableStateOf(config.screenMode) }

    // Dismiss splash screen after configured duration
    LaunchedEffect(config.splashDurationMs) {
        Handler(Looper.getMainLooper()).postDelayed({
            showSplash = false
        }, config.splashDurationMs.toLong())
    }

    // Handle system back button for WebView navigation
    BackHandler(enabled = true) {
        if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        } else {
            onClose()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                try {
                    Color(android.graphics.Color.parseColor(config.splashBgColorHex))
                } catch (e: Exception) {
                    BackgroundDark
                }
            )
            .testTag("simulator_screen_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Simulated Screen Header based on Screen Mode
            when (activeScreenMode) {
                ScreenMode.CLASSIC_APP_BAR -> {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = pageTitle.ifBlank { config.appName },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = TextPrimary
                                )
                                Text(
                                    text = currentUrl,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onClose) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Exit Simulator",
                                    tint = TextPrimary
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { webViewRef?.reload() }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload", tint = TextPrimary)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = try {
                                Color(android.graphics.Color.parseColor(config.primaryColorHex))
                            } catch (e: Exception) {
                                PrimaryIndigo
                            }
                        ),
                        modifier = Modifier.statusBarsPadding()
                    )
                }

                ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR -> {
                    // Frosted Notch Header
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .notchFadeHeader(),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Exit", tint = TextPrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = config.appName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            // Simulation badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AccentEmerald.copy(alpha = 0.2f),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = "SIMULATOR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                ScreenMode.PURE_FULLSCREEN -> {
                    // Mini floating exit chip for fullscreen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BackgroundDark.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable { onClose() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Fullscreen Simulator",
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp).size(18.dp)
                            )
                        }
                    }
                }
            }

            // Web Loading Progress Line
            if (isLoading && pageProgress < 100) {
                LinearProgressIndicator(
                    progress = { pageProgress / 100f },
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = AccentCyan,
                    trackColor = Color.Transparent
                )
            }

            // Main Web View Container
            Box(modifier = Modifier.weight(1f)) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            settings.apply {
                                javaScriptEnabled = config.enableJsInterface
                                domStorageEnabled = true
                                databaseEnabled = true
                                setSupportZoom(config.enableZoomControls)
                                builtInZoomControls = config.enableZoomControls
                                displayZoomControls = false
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                allowFileAccess = config.enableFileUpload
                                allowContentAccess = true

                                if (isDesktopMode) {
                                    userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                    url?.let { currentUrl = it }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    view?.title?.let { pageTitle = it }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    pageProgress = newProgress
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    super.onReceivedTitle(view, title)
                                    title?.let { pageTitle = it }
                                }

                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: GeolocationPermissions.Callback?
                                ) {
                                    callback?.invoke(origin, true, false)
                                }
                            }

                            loadUrl(config.targetUrl)
                            webViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Simulated Splash Overlay
                if (showSplash) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(config.splashBgColorHex))
                                } catch (e: Exception) {
                                    BackgroundDark
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        LiveSplashPreview(
                            appName = config.appName,
                            splashStyle = config.splashStyle,
                            splashBgColorHex = config.splashBgColorHex,
                            primaryColorHex = config.primaryColorHex
                        )
                    }
                }
            }

            // Bottom Simulator Quick Controls Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .dockBarSurface(),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = webViewRef?.canGoBack() == true
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Web Back",
                            tint = if (webViewRef?.canGoBack() == true) TextPrimary else TextMuted
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = webViewRef?.canGoForward() == true
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Web Forward",
                            tint = if (webViewRef?.canGoForward() == true) TextPrimary else TextMuted
                        )
                    }

                    IconButton(onClick = { webViewRef?.reload() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload", tint = AccentCyan)
                    }

                    // Toggle Desktop UA
                    IconButton(
                        onClick = {
                            isDesktopMode = !isDesktopMode
                            webViewRef?.settings?.userAgentString = if (isDesktopMode) {
                                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                            } else {
                                null
                            }
                            webViewRef?.reload()
                        }
                    ) {
                        Icon(
                            imageVector = if (isDesktopMode) Icons.Default.Laptop else Icons.Default.Smartphone,
                            contentDescription = "Toggle UA",
                            tint = if (isDesktopMode) AccentEmerald else TextSecondary
                        )
                    }

                    // Cycle Screen Mode
                    IconButton(
                        onClick = {
                            activeScreenMode = when (activeScreenMode) {
                                ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR -> ScreenMode.PURE_FULLSCREEN
                                ScreenMode.PURE_FULLSCREEN -> ScreenMode.CLASSIC_APP_BAR
                                ScreenMode.CLASSIC_APP_BAR -> ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Cycle Screen Mode",
                            tint = AccentAmber
                        )
                    }
                }
            }
        }
    }
}
