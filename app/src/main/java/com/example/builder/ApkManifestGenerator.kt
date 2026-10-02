package com.example.builder

import com.example.data.model.ScreenMode
import com.example.data.model.WebPermission
import com.example.data.model.WebProjectConfig

object ApkManifestGenerator {

    fun generateManifestXml(config: WebProjectConfig): String {
        val permissionsBlock = StringBuilder()
        // Always include INTERNET and ACCESS_NETWORK_STATE
        permissionsBlock.append("    <uses-permission android:name=\"android.permission.INTERNET\" />\n")
        permissionsBlock.append("    <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />\n")

        val permissionMap = WebPermission.values().associateBy { it.id }
        for (permId in config.enabledPermissions) {
            val perm = permissionMap[permId]
            if (perm != null && perm != WebPermission.NETWORK_STATE) {
                permissionsBlock.append("    <uses-permission android:name=\"${perm.permissionString}\" />\n")
            }
        }

        val orientationAttr = "android:configChanges=\"orientation|screenSize|screenLayout|keyboardHidden\""
        val themeAttr = when (config.screenMode) {
            ScreenMode.PURE_FULLSCREEN -> "@android:style/Theme.NoTitleBar.Fullscreen"
            ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR -> "@android:style/Theme.Material.NoActionBar"
            ScreenMode.CLASSIC_APP_BAR -> "@android:style/Theme.Material.Light.DarkActionBar"
        }

        val domainFilters = StringBuilder()
        val domains = config.whitelistedDomains.ifEmpty {
            listOf(extractHost(config.targetUrl))
        }

        for (domain in domains) {
            if (domain.isNotBlank()) {
                domainFilters.append(
                    """
                <intent-filter android:autoVerify="true">
                    <action android:name="android.intent.action.VIEW" />
                    <category android:name="android.intent.category.DEFAULT" />
                    <category android:name="android.intent.category.BROWSABLE" />
                    <data android:scheme="https" android:host="$domain" />
                    <data android:scheme="http" android:host="$domain" />
                </intent-filter>
                    """.trimIndent()
                ).append("\n")
            }
        }

        return """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="${config.packageName}"
    android:versionCode="${config.versionCode}"
    android:versionName="${config.versionName}">

$permissionsBlock
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher"
        android:supportsRtl="true"
        android:theme="$themeAttr"
        android:hardwareAccelerated="true"
        android:usesCleartextTraffic="true">

        <activity
            android:name="${config.packageName}.MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            $orientationAttr
            android:windowSoftInputMode="adjustResize">
            
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            
$domainFilters
        </activity>

    </application>
</manifest>
        """.trimIndent()
    }

    private fun extractHost(url: String): String {
        return try {
            val uri = java.net.URI(url)
            uri.host ?: "example.com"
        } catch (e: Exception) {
            "example.com"
        }
    }
}
