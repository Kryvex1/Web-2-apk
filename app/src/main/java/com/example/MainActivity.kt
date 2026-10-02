package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.builder.BuildEngineManager
import com.example.data.local.AppDatabase
import com.example.data.repository.ProjectRepository
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: ProjectRepository
    private lateinit var buildEngine: BuildEngineManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. True Edge-to-Edge Display
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        // 2. Initialize Database and Build Engine
        val database = (application as Web2ApkApp).database
        repository = ProjectRepository(database.webProjectDao())
        buildEngine = BuildEngineManager(this)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundDark),
                    color = BackgroundDark
                ) {
                    MainAppScreen(
                        repository = repository,
                        buildEngine = buildEngine
                    )
                }
            }
        }
    }
}
