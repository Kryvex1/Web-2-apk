package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScreenMode
import com.example.data.model.SplashStyle
import com.example.data.model.WebProjectConfig
import com.example.ui.components.LiveSplashPreview
import com.example.ui.theme.*

@Composable
fun Step4UiCustomisationScreen(
    config: WebProjectConfig,
    onConfigChange: (WebProjectConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColorPalette = listOf(
        "#6366F1", // Electric Indigo
        "#06B6D4", // Cyan
        "#10B981", // Emerald
        "#F59E0B", // Amber
        "#F43F5E", // Rose
        "#8B5CF6", // Violet
        "#0F172A", // Slate Dark
        "#2563EB"  // Blue
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "UI Customisation & Display Style",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Configure edge-to-edge layout, splash animations, and native WebView flags.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Screen Mode Selector Cards
        Text(
            text = "SCREEN DISPLAY MODE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentCyanLight,
            letterSpacing = 0.5.sp
        )

        ScreenMode.values().forEach { mode ->
            val isSelected = config.screenMode == mode
            val modeIcon: ImageVector = when (mode) {
                ScreenMode.EDGE_TO_EDGE_NOTCH_BLUR -> Icons.Default.BlurOn
                ScreenMode.PURE_FULLSCREEN -> Icons.Default.Fullscreen
                ScreenMode.CLASSIC_APP_BAR -> Icons.Default.Web
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onConfigChange(config.copy(screenMode = mode)) }
                    .testTag("screen_mode_${mode.name}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceCard else SurfaceDark
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) PrimaryIndigo else BorderSubtle)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PrimaryIndigo.copy(alpha = 0.25f) else SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = modeIcon,
                            contentDescription = mode.title,
                            tint = if (isSelected) AccentCyanLight else TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) AccentCyanLight else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mode.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onConfigChange(config.copy(screenMode = mode)) },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryIndigo, unselectedColor = TextMuted)
                    )
                }
            }
        }

        // Color Theme & Splash Customizer
        Card(
            modifier = Modifier.fillMaxWidth().testTag("splash_settings_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "PRIMARY THEME COLOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyanLight,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(themeColorPalette) { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = config.primaryColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onConfigChange(config.copy(primaryColorHex = hex)) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SPLASH SCREEN ANIMATION STYLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyanLight,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Splash Style selector chips
                SplashStyle.values().forEach { style ->
                    val isSelected = config.splashStyle == style
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else SurfaceDark,
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) PrimaryIndigo else BorderSubtle)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { onConfigChange(config.copy(splashStyle = style)) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = style.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) AccentCyanLight else TextPrimary
                                )
                                Text(
                                    text = style.subtitle,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { onConfigChange(config.copy(splashStyle = style)) },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryIndigo)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Duration Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Splash Duration",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${config.splashDurationMs} ms (${config.splashDurationMs / 1000f}s)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyanLight
                    )
                }

                Slider(
                    value = config.splashDurationMs.toFloat(),
                    onValueChange = { onConfigChange(config.copy(splashDurationMs = it.toInt())) },
                    valueRange = 800f..4000f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryIndigo,
                        activeTrackColor = PrimaryIndigo,
                        inactiveTrackColor = SurfaceElevated
                    ),
                    modifier = Modifier.testTag("splash_duration_slider")
                )
            }
        }

        // Live Interactive Preview Box (Required in spec)
        LiveSplashPreview(
            appName = config.appName,
            splashStyle = config.splashStyle,
            splashBgColorHex = config.splashBgColorHex,
            primaryColorHex = config.primaryColorHex
        )

        // Advanced WebView flags
        Card(
            modifier = Modifier.fillMaxWidth().testTag("advanced_webview_flags_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "ADVANCED WEBVIEW ENGINE FLAGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyanLight,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Pull to refresh
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Swipe Pull-to-Refresh", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("Enables native pull-down swipe gesture to reload web page", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = config.enablePullToRefresh,
                        onCheckedChange = { onConfigChange(config.copy(enablePullToRefresh = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryIndigo)
                    )
                }

                Divider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))

                // Desktop user agent
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Desktop User-Agent Override", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("Forces website to render full desktop layout rather than mobile", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = config.enableDesktopUserAgent,
                        onCheckedChange = { onConfigChange(config.copy(enableDesktopUserAgent = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryIndigo)
                    )
                }

                Divider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))

                // File Uploads
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("File Chooser & Camera Uploads", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("Supports <input type='file'> with Photo Picker & FileProvider", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = config.enableFileUpload,
                        onCheckedChange = { onConfigChange(config.copy(enableFileUpload = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryIndigo)
                    )
                }

                Divider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))

                // Zoom controls
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pinch-to-Zoom Support", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("Allows multi-touch pinch zooming inside the web view", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = config.enableZoomControls,
                        onCheckedChange = { onConfigChange(config.copy(enableZoomControls = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryIndigo)
                    )
                }
            }
        }
    }
}
