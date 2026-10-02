package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun WizardControlBar(
    currentStep: Int,
    totalSteps: Int = 4,
    canProceed: Boolean = true,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onBuildDirectly: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .dockBarSurface()
            .testTag("wizard_control_bar"),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Previous Button
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceElevated.copy(alpha = 0.8f),
                        contentColor = TextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("wizard_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Stage",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back", fontWeight = FontWeight.Medium)
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Next or Compile Action Button
            if (currentStep < totalSteps) {
                Button(
                    onClick = onNext,
                    enabled = canProceed,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryIndigo,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("wizard_next_button")
                ) {
                    Text("Next Step", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Stage",
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                // Final Stage: Direct Compile Button
                Button(
                    onClick = onBuildDirectly,
                    enabled = canProceed,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentEmerald,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("wizard_compile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Compile APK",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Build Native APK", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

enum class MainTab {
    BUILDER,
    HISTORY,
    SIMULATOR,
    ENGINE_CACHE
}

@Composable
fun MainBottomNavigation(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .dockBarSurface()
            .testTag("main_bottom_navigation"),
        color = Color.Transparent
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.navigationBarsPadding(),
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = selectedTab == MainTab.BUILDER,
                onClick = { onTabSelected(MainTab.BUILDER) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Builder Wizard"
                    )
                },
                label = { Text("Builder", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentCyanLight,
                    selectedTextColor = AccentCyanLight,
                    indicatorColor = PrimaryIndigoDark.copy(alpha = 0.5f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )

            NavigationBarItem(
                selected = selectedTab == MainTab.SIMULATOR,
                onClick = { onTabSelected(MainTab.SIMULATOR) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = "Live Simulator"
                    )
                },
                label = { Text("Simulator", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentCyan,
                    selectedTextColor = AccentCyanLight,
                    indicatorColor = AccentCyan.copy(alpha = 0.2f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )

            NavigationBarItem(
                selected = selectedTab == MainTab.HISTORY,
                onClick = { onTabSelected(MainTab.HISTORY) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Projects & History"
                    )
                },
                label = { Text("Projects", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentEmerald,
                    selectedTextColor = AccentEmerald,
                    indicatorColor = AccentEmerald.copy(alpha = 0.2f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )

            NavigationBarItem(
                selected = selectedTab == MainTab.ENGINE_CACHE,
                onClick = { onTabSelected(MainTab.ENGINE_CACHE) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Engine Cache"
                    )
                },
                label = { Text("Engine", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentAmber,
                    selectedTextColor = AccentAmber,
                    indicatorColor = AccentAmber.copy(alpha = 0.2f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
