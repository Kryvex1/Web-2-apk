package com.example.data.model

enum class ScreenMode(val title: String, val description: String) {
    EDGE_TO_EDGE_NOTCH_BLUR(
        title = "Edge-to-Edge Notch Blur",
        description = "Content bleeds smoothly behind the notch & status bar with a dynamic frosted blur effect."
    ),
    PURE_FULLSCREEN(
        title = "Pure Fullscreen (Immersive)",
        description = "Sticky immersive mode hiding status and navigation bars for games or full web apps."
    ),
    CLASSIC_APP_BAR(
        title = "Classic App Bar",
        description = "Native top navigation bar with custom primary color, title, back/forward & reload actions."
    )
}

enum class SplashStyle(val title: String, val subtitle: String) {
    LOGO_PULSE("Pulse App Logo", "Breathing scale animation with glowing aura"),
    SHIMMER_SKELETON("Shimmer Skeleton", "High-tech linear metallic shimmer effect"),
    MODERN_SPINNER("Modern Circular Spinner", "Fluid gradient radial spinning loader"),
    TITLE_GLOW("Animated Title Glow", "Neon gradient title text fade & glow"),
    PROGRESS_BAR("Neon Progress Bar", "Sleek edge-aligned progress loader at top/center")
}

enum class CachingPolicy(val title: String, val description: String) {
    NETWORK_FIRST("Network First (Online Live)", "Fetches latest online version, falls back to cache if offline"),
    CACHE_FIRST("Cache First (High Speed)", "Loads cached static assets instantly with background sync"),
    OFFLINE_SNAPSHOT("Offline Snapshot Fallback", "Includes offline landing page when device is completely disconnected")
}

enum class WebPermission(
    val id: String,
    val permissionString: String,
    val title: String,
    val description: String,
    val isDefaultBrowser: Boolean
) {
    CAMERA(
        id = "camera",
        permissionString = "android.permission.CAMERA",
        title = "Camera Access",
        description = "Enables barcode scanning, photo capture, and WebRTC video calls",
        isDefaultBrowser = true
    ),
    LOCATION(
        id = "location",
        permissionString = "android.permission.ACCESS_FINE_LOCATION",
        title = "GPS & Geolocation",
        description = "Allows HTML5 Geolocation API for map coordinates & navigation",
        isDefaultBrowser = true
    ),
    STORAGE(
        id = "storage",
        permissionString = "android.permission.READ_MEDIA_IMAGES",
        title = "Storage & Photo Picker",
        description = "Permits file uploads, attachments, and downloading documents",
        isDefaultBrowser = true
    ),
    MICROPHONE(
        id = "microphone",
        permissionString = "android.permission.RECORD_AUDIO",
        title = "Microphone & Audio",
        description = "Enables voice search, WebRTC audio calls, and audio recording",
        isDefaultBrowser = true
    ),
    NOTIFICATIONS(
        id = "notifications",
        permissionString = "android.permission.POST_NOTIFICATIONS",
        title = "Push Notifications",
        description = "Receives web push notifications and background updates",
        isDefaultBrowser = true
    ),
    BIOMETRICS(
        id = "biometrics",
        permissionString = "android.permission.USE_BIOMETRIC",
        title = "Biometrics & WebAuthn",
        description = "Fingerprint / Face unlock for passwordless login",
        isDefaultBrowser = false
    ),
    NETWORK_STATE(
        id = "network_state",
        permissionString = "android.permission.ACCESS_NETWORK_STATE",
        title = "Network State Monitor",
        description = "Detects Wi-Fi/Cellular connectivity changes seamlessly",
        isDefaultBrowser = true
    ),
    VIBRATION(
        id = "vibration",
        permissionString = "android.permission.VIBRATE",
        title = "Haptic Vibration",
        description = "Supports navigator.vibrate() for interactive button feedback",
        isDefaultBrowser = true
    ),
    WAKE_LOCK(
        id = "wake_lock",
        permissionString = "android.permission.WAKE_LOCK",
        title = "Screen Wake Lock",
        description = "Prevents device from sleeping during video playback or live streams",
        isDefaultBrowser = false
    ),
    BACKGROUND_AUDIO(
        id = "background_audio",
        permissionString = "android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK",
        title = "Background Audio",
        description = "Keeps podcast / music streaming active when app is minimized",
        isDefaultBrowser = false
    )
}

data class WebProjectConfig(
    val id: Long = 0,
    val appName: String = "My Web App",
    val targetUrl: String = "https://example.com",
    val packageName: String = "com.company.myapp",
    val versionName: String = "1.0.0",
    val versionCode: Int = 1,
    val screenMode: ScreenMode = ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR,
    val splashStyle: SplashStyle = SplashStyle.LOGO_PULSE,
    val splashDurationMs: Int = 2000,
    val splashBgColorHex: String = "#090D16",
    val primaryColorHex: String = "#6366F1",
    val cachingPolicy: CachingPolicy = CachingPolicy.NETWORK_FIRST,
    val isDefaultBrowserMode: Boolean = true,
    val enabledPermissions: Set<String> = WebPermission.values().filter { it.isDefaultBrowser }.map { it.id }.toSet(),
    val whitelistedDomains: List<String> = emptyList(),
    val enablePullToRefresh: Boolean = true,
    val enableZoomControls: Boolean = false,
    val enableJsInterface: Boolean = true,
    val enableFileUpload: Boolean = true,
    val enableDesktopUserAgent: Boolean = false,
    val customUserAgent: String = "",
    val iconBitmapPath: String? = null,
    val generatedApkPath: String? = null,
    val apkSizeBytes: Long = 0L,
    val lastBuildTime: Long = 0L
)
