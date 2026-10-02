package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CachingPolicy
import com.example.data.model.ScreenMode
import com.example.data.model.SplashStyle
import com.example.data.model.WebProjectConfig

@Entity(tableName = "web_projects")
data class WebProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appName: String,
    val targetUrl: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val screenMode: String,
    val splashStyle: String,
    val splashDurationMs: Int,
    val splashBgColorHex: String,
    val primaryColorHex: String,
    val cachingPolicy: String,
    val isDefaultBrowserMode: Boolean,
    val enabledPermissionsCommaSeparated: String,
    val whitelistedDomainsCommaSeparated: String,
    val enablePullToRefresh: Boolean,
    val enableZoomControls: Boolean,
    val enableJsInterface: Boolean,
    val enableFileUpload: Boolean,
    val enableDesktopUserAgent: Boolean,
    val customUserAgent: String,
    val iconBitmapPath: String?,
    val generatedApkPath: String?,
    val apkSizeBytes: Long,
    val lastBuildTime: Long
) {
    fun toConfig(): WebProjectConfig {
        return WebProjectConfig(
            id = id,
            appName = appName,
            targetUrl = targetUrl,
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,
            screenMode = try { ScreenMode.valueOf(screenMode) } catch (e: Exception) { ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR },
            splashStyle = try { SplashStyle.valueOf(splashStyle) } catch (e: Exception) { SplashStyle.LOGO_PULSE },
            splashDurationMs = splashDurationMs,
            splashBgColorHex = splashBgColorHex,
            primaryColorHex = primaryColorHex,
            cachingPolicy = try { CachingPolicy.valueOf(cachingPolicy) } catch (e: Exception) { CachingPolicy.NETWORK_FIRST },
            isDefaultBrowserMode = isDefaultBrowserMode,
            enabledPermissions = if (enabledPermissionsCommaSeparated.isBlank()) emptySet() else enabledPermissionsCommaSeparated.split(",").toSet(),
            whitelistedDomains = if (whitelistedDomainsCommaSeparated.isBlank()) emptyList() else whitelistedDomainsCommaSeparated.split(","),
            enablePullToRefresh = enablePullToRefresh,
            enableZoomControls = enableZoomControls,
            enableJsInterface = enableJsInterface,
            enableFileUpload = enableFileUpload,
            enableDesktopUserAgent = enableDesktopUserAgent,
            customUserAgent = customUserAgent,
            iconBitmapPath = iconBitmapPath,
            generatedApkPath = generatedApkPath,
            apkSizeBytes = apkSizeBytes,
            lastBuildTime = lastBuildTime
        )
    }

    companion object {
        fun fromConfig(config: WebProjectConfig): WebProjectEntity {
            return WebProjectEntity(
                id = config.id,
                appName = config.appName,
                targetUrl = config.targetUrl,
                packageName = config.packageName,
                versionName = config.versionName,
                versionCode = config.versionCode,
                screenMode = config.screenMode.name,
                splashStyle = config.splashStyle.name,
                splashDurationMs = config.splashDurationMs,
                splashBgColorHex = config.splashBgColorHex,
                primaryColorHex = config.primaryColorHex,
                cachingPolicy = config.cachingPolicy.name,
                isDefaultBrowserMode = config.isDefaultBrowserMode,
                enabledPermissionsCommaSeparated = config.enabledPermissions.joinToString(","),
                whitelistedDomainsCommaSeparated = config.whitelistedDomains.joinToString(","),
                enablePullToRefresh = config.enablePullToRefresh,
                enableZoomControls = config.enableZoomControls,
                enableJsInterface = config.enableJsInterface,
                enableFileUpload = config.enableFileUpload,
                enableDesktopUserAgent = config.enableDesktopUserAgent,
                customUserAgent = config.customUserAgent,
                iconBitmapPath = config.iconBitmapPath,
                generatedApkPath = config.generatedApkPath,
                apkSizeBytes = config.apkSizeBytes,
                lastBuildTime = config.lastBuildTime
            )
        }
    }
}
