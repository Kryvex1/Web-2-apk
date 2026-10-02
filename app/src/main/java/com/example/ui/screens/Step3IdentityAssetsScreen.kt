package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WebProjectConfig
import com.example.ui.components.IconCropperDialog
import com.example.ui.theme.*
import com.example.utils.NetworkUtils
import java.io.File

@Composable
fun Step3IdentityAssetsScreen(
    config: WebProjectConfig,
    onConfigChange: (WebProjectConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var showIconCropper by remember { mutableStateOf(false) }

    val isPackageValid = remember(config.packageName) {
        config.packageName.matches(Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$"))
    }

    val iconBitmap = remember(config.iconBitmapPath) {
        if (!config.iconBitmapPath.isNullOrBlank()) {
            try {
                BitmapFactory.decodeFile(config.iconBitmapPath)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Identity & Visual Assets",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Set the application launcher label, unique package namespace, and high-density icons.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // App Icon Card with launcher for IconCropperDialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_icon_picker_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderHighlight))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Display
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(config.primaryColorHex))
                            } catch (e: Exception) {
                                PrimaryIndigo
                            }
                        )
                        .clickable { showIconCropper = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (iconBitmap != null) {
                        Image(
                            bitmap = iconBitmap.asImageBitmap(),
                            contentDescription = "Custom App Icon",
                            modifier = Modifier.size(60.dp)
                        )
                    } else {
                        Text(
                            text = if (config.appName.isNotBlank()) config.appName.take(1).uppercase() else "W",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LAUNCHER ICON ASSET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyanLight,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (iconBitmap != null) "Customized (Ready for mdpi-xxxhdpi)" else "Default Vector Badge (Tap to customize)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { showIconCropper = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = AccentCyanLight),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("open_icon_designer_button")
                    ) {
                        Icon(imageVector = Icons.Default.Crop, contentDescription = "Edit Icon", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Icon Studio", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // App Name Input
        Card(
            modifier = Modifier.fillMaxWidth().testTag("app_name_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "APPLICATION LABEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyanLight,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = config.appName,
                    onValueChange = { name ->
                        val domain = NetworkUtils.extractDomain(config.targetUrl)
                        val suggested = NetworkUtils.suggestPackageName(domain, name)
                        onConfigChange(config.copy(appName = name, packageName = suggested))
                    },
                    placeholder = { Text("e.g. My Web Store", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("app_name_input_field")
                )
            }
        }

        // Package Name Input
        Card(
            modifier = Modifier.fillMaxWidth().testTag("package_name_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(if (isPackageValid) BorderSubtle else AccentRose)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PACKAGE IDENTIFIER (APPLICATION ID)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyanLight,
                        letterSpacing = 0.5.sp
                    )
                    if (isPackageValid) {
                        Text("Valid Namespace", fontSize = 10.sp, color = AccentEmerald)
                    } else {
                        Text("Invalid Format", fontSize = 10.sp, color = AccentRose)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = config.packageName,
                    onValueChange = { onConfigChange(config.copy(packageName = it.trim().lowercase())) },
                    placeholder = { Text("com.company.myapp", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isPackageValid) PrimaryIndigo else AccentRose,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("package_name_input_field")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Format: com.yourcompany.app (Must contain at least two segments)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Version Name and Version Code
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Version Name
            Card(
                modifier = Modifier.weight(1f).testTag("version_name_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "VERSION NAME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyanLight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = config.versionName,
                        onValueChange = { onConfigChange(config.copy(versionName = it.trim())) },
                        placeholder = { Text("1.0.0", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("version_name_input_field")
                    )
                }
            }

            // Version Code
            Card(
                modifier = Modifier.weight(1f).testTag("version_code_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "VERSION CODE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyanLight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = "${config.versionCode}",
                        onValueChange = { input ->
                            val code = input.filter { it.isDigit() }.toIntOrNull() ?: 1
                            onConfigChange(config.copy(versionCode = code))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("version_code_input_field")
                    )
                }
            }
        }
    }

    if (showIconCropper) {
        IconCropperDialog(
            appName = config.appName,
            initialPrimaryColor = config.primaryColorHex,
            onDismiss = { showIconCropper = false },
            onIconSaved = { path ->
                onConfigChange(config.copy(iconBitmapPath = path))
            }
        )
    }
}
