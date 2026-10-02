package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.builder.BuildEngineManager
import com.example.data.model.WebProjectConfig
import com.example.data.repository.ProjectRepository
import com.example.ui.components.GlassTopBar
import com.example.ui.components.MainBottomNavigation
import com.example.ui.components.MainTab
import com.example.ui.components.StepProgressIndicator
import com.example.ui.components.WizardControlBar
import com.example.ui.theme.BackgroundDark
import com.example.utils.NetworkUtils
import kotlinx.coroutines.launch

@Composable
fun MainAppScreen(
    repository: ProjectRepository,
    buildEngine: BuildEngineManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(MainTab.BUILDER) }
    var currentStep by remember { mutableIntStateOf(1) }
    var activeConfig by remember { mutableStateOf(WebProjectConfig()) }
    var isSimulating by remember { mutableStateOf(false) }

    // Check if current stage requirements are met
    val canProceed = remember(currentStep, activeConfig) {
        when (currentStep) {
            1 -> NetworkUtils.isValidUrl(activeConfig.targetUrl)
            2 -> activeConfig.enabledPermissions.isNotEmpty()
            3 -> activeConfig.appName.isNotBlank() &&
                    activeConfig.packageName.matches(Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$"))
            4 -> true
            else -> true
        }
    }

    BackHandler(enabled = currentStep > 1 && selectedTab == MainTab.BUILDER) {
        if (currentStep > 1) {
            currentStep--
        }
    }

    if (isSimulating) {
        SimulatorScreen(
            config = activeConfig,
            onClose = { isSimulating = false }
        )
        return
    }

    Scaffold(
        topBar = {
            val title = when (selectedTab) {
                MainTab.BUILDER -> if (currentStep == 5) "Native APK Builder" else "Web2APK Pro"
                MainTab.SIMULATOR -> "Live Simulator"
                MainTab.HISTORY -> "Projects & Builds"
                MainTab.ENGINE_CACHE -> "Engine Cache"
            }
            val subtitle = when (selectedTab) {
                MainTab.BUILDER -> if (currentStep == 5) "Compiling APK Package" else "Native Android Engine"
                MainTab.SIMULATOR -> "In-App Runtime Container"
                MainTab.HISTORY -> "Room Persistence Hub"
                MainTab.ENGINE_CACHE -> "Offline Toolchain"
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                GlassTopBar(
                    title = title,
                    subtitle = subtitle,
                    showBackButton = (selectedTab == MainTab.BUILDER && currentStep > 1),
                    onBackClick = {
                        if (currentStep > 1) currentStep--
                    },
                    onSimulatorClick = {
                        isSimulating = true
                    },
                    onSettingsClick = {
                        selectedTab = MainTab.ENGINE_CACHE
                    }
                )

                if (selectedTab == MainTab.BUILDER && currentStep in 1..4) {
                    StepProgressIndicator(
                        currentStep = currentStep,
                        totalSteps = 4,
                        onStepClick = { step -> currentStep = step }
                    )
                }
            }
        },
        bottomBar = {
            if (selectedTab == MainTab.BUILDER && currentStep in 1..4) {
                WizardControlBar(
                    currentStep = currentStep,
                    totalSteps = 4,
                    canProceed = canProceed,
                    onBack = { if (currentStep > 1) currentStep-- },
                    onNext = { if (currentStep < 4) currentStep++ },
                    onBuildDirectly = {
                        currentStep = 5
                        coroutineScope.launch {
                            val result = buildEngine.buildApk(activeConfig)
                            if (result.isSuccess) {
                                val apk = result.getOrNull()
                                val updatedConfig = activeConfig.copy(
                                    generatedApkPath = apk?.absolutePath,
                                    apkSizeBytes = apk?.length() ?: 0L,
                                    lastBuildTime = System.currentTimeMillis()
                                )
                                activeConfig = updatedConfig
                                repository.saveProject(updatedConfig)
                            }
                        }
                    }
                )
            } else {
                MainBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        if (tab == MainTab.BUILDER && currentStep == 5) {
                            currentStep = 4
                        }
                    }
                )
            }
        },
        containerColor = BackgroundDark,
        modifier = modifier.fillMaxSize().testTag("main_app_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.BUILDER -> {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally(tween(300)) { width -> width } + fadeIn(tween(300)))
                                    .togetherWith(slideOutHorizontally(tween(300)) { width -> -width } + fadeOut(tween(300)))
                            } else {
                                (slideInHorizontally(tween(300)) { width -> -width } + fadeIn(tween(300)))
                                    .togetherWith(slideOutHorizontally(tween(300)) { width -> width } + fadeOut(tween(300)))
                            }
                        },
                        label = "step_transition"
                    ) { step ->
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            when (step) {
                                1 -> Step1UrlScopeScreen(
                                    config = activeConfig,
                                    onConfigChange = { activeConfig = it }
                                )
                                2 -> Step2PermissionsScreen(
                                    config = activeConfig,
                                    onConfigChange = { activeConfig = it }
                                )
                                3 -> Step3IdentityAssetsScreen(
                                    config = activeConfig,
                                    onConfigChange = { activeConfig = it }
                                )
                                4 -> Step4UiCustomisationScreen(
                                    config = activeConfig,
                                    onConfigChange = { activeConfig = it }
                                )
                                5 -> BuildProgressScreen(
                                    config = activeConfig,
                                    buildEngine = buildEngine,
                                    onLaunchSimulator = { isSimulating = true },
                                    onEditConfig = { currentStep = 4 }
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                MainTab.SIMULATOR -> {
                    SimulatorScreen(
                        config = activeConfig,
                        onClose = { selectedTab = MainTab.BUILDER }
                    )
                }

                MainTab.HISTORY -> {
                    HistoryScreen(
                        repository = repository,
                        onLoadProjectToWizard = { loaded ->
                            activeConfig = loaded
                            selectedTab = MainTab.BUILDER
                            currentStep = 1
                        },
                        onRebuildDirectly = { project ->
                            activeConfig = project
                            selectedTab = MainTab.BUILDER
                            currentStep = 5
                            coroutineScope.launch {
                                val result = buildEngine.buildApk(project)
                                if (result.isSuccess) {
                                    val apk = result.getOrNull()
                                    val updated = project.copy(
                                        generatedApkPath = apk?.absolutePath,
                                        apkSizeBytes = apk?.length() ?: 0L,
                                        lastBuildTime = System.currentTimeMillis()
                                    )
                                    repository.saveProject(updated)
                                }
                            }
                        }
                    )
                }

                MainTab.ENGINE_CACHE -> {
                    SettingsEngineScreen(buildEngine = buildEngine)
                }
            }
        }
    }
}
