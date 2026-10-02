package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WebPermission
import com.example.data.model.WebProjectConfig
import com.example.ui.components.PermissionItemCard
import com.example.ui.theme.*

@Composable
fun Step2PermissionsScreen(
    config: WebProjectConfig,
    onConfigChange: (WebProjectConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var showManifestPreview by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Column {
            Text(
                text = "Permissions & Device Features",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Configure hardware capabilities & web permission handlers injected into the APK.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Mode Switcher Card (Default Browser Mode vs Custom Permissions Mode)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("permissions_mode_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderHighlight))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PRESET ENGINE MODE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyanLight,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Toggle Button
                    Switch(
                        checked = config.isDefaultBrowserMode,
                        onCheckedChange = { isDefault ->
                            val newPermissions = if (isDefault) {
                                WebPermission.values().filter { it.isDefaultBrowser }.map { it.id }.toSet()
                            } else {
                                config.enabledPermissions
                            }
                            onConfigChange(
                                config.copy(
                                    isDefaultBrowserMode = isDefault,
                                    enabledPermissions = newPermissions
                                )
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryIndigo,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceElevated
                        ),
                        modifier = Modifier.testTag("browser_mode_switch")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (config.isDefaultBrowserMode) {
                    Text(
                        text = "Standard Browser Mode Active",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentEmerald
                    )
                    Text(
                        text = "Automatically grants standard web permissions (Camera, Location, Storage, Audio, Network State, Notifications) with runtime WebChromeClient prompts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    Text(
                        text = "Custom Permissions Mode Active",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentAmber
                    )
                    Text(
                        text = "Fine-tune and audit individual permissions. Only selected items will be declared in the manifest.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Live Manifest Preview Toggle
        OutlinedButton(
            onClick = { showManifestPreview = !showManifestPreview },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = SurfaceDark,
                contentColor = AccentCyanLight
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)
            ),
            modifier = Modifier.fillMaxWidth().testTag("toggle_manifest_preview_button")
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Code Preview",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (showManifestPreview) "Hide Manifest Permissions XML" else "Preview Injected AndroidManifest.xml (${config.enabledPermissions.size} Active)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        AnimatedVisibility(visible = showManifestPreview) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalBg),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "<!-- Injected Permissions -->\n" +
                                "<uses-permission android:name=\"android.permission.INTERNET\" />\n" +
                                "<uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />\n" +
                                config.enabledPermissions.mapNotNull { id ->
                                    val p = WebPermission.values().firstOrNull { it.id == id }
                                    if (p != null && p != WebPermission.NETWORK_STATE) {
                                        "<uses-permission android:name=\"${p.permissionString}\" />"
                                    } else null
                                }.joinToString("\n"),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = AccentCyanLight
                    )
                }
            }
        }

        // Itemized Permissions List
        Text(
            text = "INDIVIDUAL PERMISSION MATRIX",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentCyanLight,
            letterSpacing = 0.5.sp
        )

        WebPermission.values().forEach { permission ->
            val isSelected = config.enabledPermissions.contains(permission.id)
            PermissionItemCard(
                permission = permission,
                isSelected = isSelected,
                onToggle = { checked ->
                    val updated = if (checked) {
                        config.enabledPermissions + permission.id
                    } else {
                        config.enabledPermissions - permission.id
                    }
                    onConfigChange(
                        config.copy(
                            enabledPermissions = updated,
                            isDefaultBrowserMode = false // Switch to custom if user manually edits
                        )
                    )
                }
            )
        }
    }
}
