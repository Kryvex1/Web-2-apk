package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WebProjectConfig
import com.example.data.repository.ProjectRepository
import com.example.ui.theme.*
import com.example.utils.ApkInstaller
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    repository: ProjectRepository,
    onLoadProjectToWizard: (WebProjectConfig) -> Unit,
    onRebuildDirectly: (WebProjectConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedProjects by repository.allProjectsFlow.collectAsState(initial = emptyList())
    val starterPresets = remember { repository.getStarterPresets() }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Project Hub & History",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Manage saved Web2APK configurations, starter templates, and compiled APK packages.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Quick Starter Presets Section
        item {
            Text(
                text = "STARTER TEMPLATES & PRESETS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyanLight,
                letterSpacing = 0.5.sp
            )
        }

        items(starterPresets) { preset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLoadProjectToWizard(preset) }
                    .testTag("preset_card_${preset.appName.take(6)}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderHighlight))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(preset.primaryColorHex))
                                } catch (e: Exception) {
                                    PrimaryIndigo
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset.appName.take(1),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = preset.appName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SurfaceElevated
                            ) {
                                Text(
                                    text = preset.screenMode.title.take(12),
                                    fontSize = 9.sp,
                                    color = AccentCyanLight,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = preset.targetUrl,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { onLoadProjectToWizard(preset) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Use Preset", fontSize = 11.sp)
                    }
                }
            }
        }

        // Saved Builds & Projects Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SAVED & COMPILED APKS (${savedProjects.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyanLight,
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (savedProjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.FolderOpen, contentDescription = "Empty", tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Saved Projects Yet", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Build your first APK from the Builder Wizard tab.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(savedProjects) { project ->
                val apkFile = project.generatedApkPath?.let { File(it) }
                val apkExists = apkFile?.exists() == true

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("saved_project_${project.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryIndigoDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Android, contentDescription = "APK", tint = AccentEmerald, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = project.appName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${project.packageName} • v${project.versionName}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextMuted
                                    )
                                }
                            }

                            // Delete button
                            IconButton(
                                onClick = {
                                    coroutineScope.launch { repository.deleteProject(project.id) }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "URL: ${project.targetUrl}",
                            fontSize = 11.sp,
                            color = AccentCyanLight,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Load to Wizard
                            Button(
                                onClick = { onLoadProjectToWizard(project) },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = TextPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, "Edit", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Config", fontSize = 11.sp)
                            }

                            // Install or Rebuild
                            if (apkExists && apkFile != null) {
                                Button(
                                    onClick = { ApkInstaller.installApk(context, apkFile) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald, contentColor = Color.Black),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Icon(Icons.Default.InstallMobile, "Install", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Install APK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onRebuildDirectly(project) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo, contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, "Rebuild", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rebuild APK", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
