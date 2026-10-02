package com.example.data.repository

import com.example.data.local.WebProjectDao
import com.example.data.local.WebProjectEntity
import com.example.data.model.CachingPolicy
import com.example.data.model.ScreenMode
import com.example.data.model.SplashStyle
import com.example.data.model.WebPermission
import com.example.data.model.WebProjectConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(private val dao: WebProjectDao) {

    val allProjectsFlow: Flow<List<WebProjectConfig>> = dao.getAllProjectsFlow().map { entities ->
        entities.map { it.toConfig() }
    }

    suspend fun getProjectById(id: Long): WebProjectConfig? {
        return dao.getProjectById(id)?.toConfig()
    }

    suspend fun saveProject(config: WebProjectConfig): Long {
        val entity = WebProjectEntity.fromConfig(config)
        return dao.insertProject(entity)
    }

    suspend fun deleteProject(id: Long) {
        dao.deleteById(id)
    }

    fun getStarterPresets(): List<WebProjectConfig> {
        return listOf(
            WebProjectConfig(
                appName = "ShopSphere E-Commerce",
                targetUrl = "https://demo.ecommerceshop.com",
                packageName = "com.shopsphere.app",
                versionName = "1.0.0",
                versionCode = 1,
                screenMode = ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR,
                splashStyle = SplashStyle.LOGO_PULSE,
                splashBgColorHex = "#0F172A",
                primaryColorHex = "#6366F1",
                isDefaultBrowserMode = true,
                enabledPermissions = setOf("camera", "location", "storage", "notifications", "network_state", "vibration")
            ),
            WebProjectConfig(
                appName = "Retro Arcade 3D",
                targetUrl = "https://play2048.co",
                packageName = "com.retrogame.arcade",
                versionName = "2.1.0",
                versionCode = 21,
                screenMode = ScreenMode.PURE_FULLSCREEN,
                splashStyle = SplashStyle.SHIMMER_SKELETON,
                splashBgColorHex = "#050811",
                primaryColorHex = "#10B981",
                isDefaultBrowserMode = false,
                enabledPermissions = setOf("vibration", "network_state", "wake_lock")
            ),
            WebProjectConfig(
                appName = "FinTech Portal",
                targetUrl = "https://app.dashboardanalytics.io",
                packageName = "com.fintech.portal",
                versionName = "3.0.0",
                versionCode = 300,
                screenMode = ScreenMode.CLASSIC_APP_BAR,
                splashStyle = SplashStyle.MODERN_SPINNER,
                splashBgColorHex = "#111827",
                primaryColorHex = "#06B6D4",
                isDefaultBrowserMode = true,
                enabledPermissions = setOf("biometrics", "notifications", "network_state", "storage")
            )
        )
    }
}
