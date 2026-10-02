package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.builder.BuildEngineManager
import com.example.builder.BuildLogEntry
import com.example.data.model.WebProjectConfig
import com.example.ui.components.TerminalBuildLogView
import com.example.ui.theme.*
import com.example.utils.ApkInstaller

@Composable
fun BuildProgressScreen(
    config: WebProjectConfig,
    buildEngine: BuildEngineManager,
    onLaunchSimulator: () -> Unit,
    onEditConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBuilding by buildEngine.isBuilding.collectAsState()
    val currentProgress by buildEngine.currentProgress.collectAsState()
    val currentStep by buildEngine.currentStep.collectAsState()
    val logs by buildEngine.buildLogs.collectAsState()
    val generatedApk by buildEngine.generatedApkFile.collectAsState()
    val generatedZip by buildEngine.generatedProjectZip.collectAsState()
    val sha256 by buildEngine.apkSha256.collectAsState()

    val stepTitles = listOf(
        "Initializing Offline Toolchain",
        "Synthesizing AndroidManifest & Permissions",
        "Generating Multi-Density Assets (mdpi-xxxhdpi)",
        "Compiling WebView Runtime & DEX Bytecode",
        "Aligning ZIP & Signing Cryptographic Package",
        "Finalizing Native APK & SHA-256 Digest"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Build Status Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("build_status_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    if (generatedApk != null) listOf(AccentEmerald, PrimaryIndigo)
                    else listOf(BorderHighlight, PrimaryIndigo)
                )
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isBuilding) {
                            val infiniteTransition = rememberInfiniteTransition(label = "spin")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing)),
                                label = "rot"
                            )
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Compiling",
                                tint = AccentCyan,
                                modifier = Modifier.size(24.dp).rotate(rotation)
                            )
                        } else if (generatedApk != null) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AccentEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Ready",
                                tint = PrimaryIndigoLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isBuilding) "COMPILING NATIVE ANDROID APK..."
                                else if (generatedApk != null) "APK COMPILED SUCCESSFULLY"
                                else "READY TO BUILD",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (generatedApk != null) AccentEmerald else TextPrimary
                            )
                            Text(
                                text = if (isBuilding) stepTitles.getOrElse(currentStep - 1) { "Processing..." }
                                else if (generatedApk != null) "${generatedApk!!.name} • ${(generatedApk!!.length() / 1024)} KB"
                                else "All settings configured",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "${(currentProgress * 100).toInt()}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (generatedApk != null) AccentEmerald else AccentCyanLight
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { currentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = if (generatedApk != null) AccentEmerald else PrimaryIndigo,
                    trackColor = SurfaceElevated
                )
            }
        }

        // Real-Time Terminal View
        TerminalBuildLogView(logs = logs)

        // Post-Build Action Buttons (Instant Install, Share, Run in Simulator, Export Source)
        if (generatedApk != null) {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("post_build_actions_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderHighlight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PACKAGE ARTIFACT ACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyanLight,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary: Install APK via FileProvider
                    Button(
                        onClick = { ApkInstaller.installApk(context, generatedApk!!) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentEmerald,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("install_apk_button")
                    ) {
                        Icon(imageVector = Icons.Default.InstallMobile, contentDescription = "Install", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Install App on Device", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Live In-App Simulator
                        Button(
                            onClick = onLaunchSimulator,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryIndigo,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("launch_simulator_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Simulator", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test in Simulator", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Share APK
                        OutlinedButton(
                            onClick = {
                                ApkInstaller.shareFile(
                                    context = context,
                                    file = generatedApk!!,
                                    mimeType = "application/vnd.android.package-archive",
                                    title = "Share ${config.appName} APK"
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceElevated,
                                contentColor = TextPrimary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)
                            ),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("share_apk_button")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share APK", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Export Full Android Studio Project Source ZIP
                    if (generatedZip != null) {
                        OutlinedButton(
                            onClick = {
                                ApkInstaller.shareFile(
                                    context = context,
                                    file = generatedZip!!,
                                    mimeType = "application/zip",
                                    title = "Export Android Studio Project Bundle"
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceDark,
                                contentColor = AccentCyanLight
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)
                            ),
                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("export_project_zip_button")
                        ) {
                            Icon(imageVector = Icons.Default.FolderZip, contentDescription = "Export ZIP", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Android Studio Source Code (.zip)", fontSize = 12.sp)
                        }
                    }

                    // SHA-256 Digest Card
                    if (sha256.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TerminalBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "SHA-256 VERIFIED SIGNATURE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sha256,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = AccentCyanLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
