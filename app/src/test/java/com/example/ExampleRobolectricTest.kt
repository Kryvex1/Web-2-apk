package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.builder.ApkManifestGenerator
import com.example.builder.BuildEngineManager
import com.example.data.local.AppDatabase
import com.example.data.model.ScreenMode
import com.example.data.model.WebProjectConfig
import com.example.data.repository.ProjectRepository
import com.example.utils.NetworkUtils
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Web2APK Pro", appName)
    }

    @Test
    fun `network utilities validate url and suggest package name`() {
        val url = "https://ecommerce.store.com"
        assertTrue(NetworkUtils.isValidUrl(url))
        val domain = NetworkUtils.extractDomain(url)
        assertEquals("ecommerce.store.com", domain)

        val pkg = NetworkUtils.suggestPackageName(domain, "ShopApp")
        assertTrue(pkg.startsWith("com.store") || pkg.contains("shopapp"))
    }

    @Test
    fun `manifest generator injects package name and permissions`() {
        val config = WebProjectConfig(
            appName = "Test App",
            targetUrl = "https://myportal.com",
            packageName = "com.test.portal",
            versionName = "1.2.0",
            versionCode = 12,
            screenMode = ScreenMode.PURE_FULLSCREEN,
            enabledPermissions = setOf("camera", "location", "storage")
        )

        val manifestXml = ApkManifestGenerator.generateManifestXml(config)
        assertTrue(manifestXml.contains("package=\"com.test.portal\""))
        assertTrue(manifestXml.contains("android:versionName=\"1.2.0\""))
        assertTrue(manifestXml.contains("android.permission.CAMERA"))
        assertTrue(manifestXml.contains("android.permission.ACCESS_FINE_LOCATION"))
    }

    @Test
    fun `build engine creates cached tools and compiles apk`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = BuildEngineManager(context)
        assertTrue(engine.isEngineReady.value)

        val config = WebProjectConfig(
            appName = "Unit Test App",
            targetUrl = "https://example.org",
            packageName = "com.unittest.app",
            versionName = "1.0.0",
            versionCode = 1
        )

        val result = engine.buildApk(config)
        assertTrue(result.isSuccess)
        val apk = result.getOrNull()
        assertNotNull(apk)
        assertTrue(apk!!.exists())
        assertTrue(apk.length() > 0)
    }

    @Test
    fun `repository saves and loads project config`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repo = ProjectRepository(db.webProjectDao())

        val config = WebProjectConfig(
            appName = "Persistence Test",
            targetUrl = "https://persisted.com",
            packageName = "com.persist.app"
        )

        val id = repo.saveProject(config)
        assertTrue(id > 0)

        val loaded = repo.getProjectById(id)
        assertNotNull(loaded)
        assertEquals("Persistence Test", loaded?.appName)
        assertEquals("https://persisted.com", loaded?.targetUrl)
    }
}
