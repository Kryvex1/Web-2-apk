package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CachingPolicy
import com.example.data.model.WebProjectConfig
import com.example.ui.components.DomainWhitelistEditor
import com.example.ui.theme.*
import com.example.utils.NetworkUtils
import kotlinx.coroutines.launch

@Composable
fun Step1UrlScopeScreen(
    config: WebProjectConfig,
    onConfigChange: (WebProjectConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var faviconBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isCheckingFavicon by remember { mutableStateOf(false) }

    val isValid = remember(config.targetUrl) {
        NetworkUtils.isValidUrl(config.targetUrl)
    }

    // Auto-fetch favicon on valid URL change
    LaunchedEffect(config.targetUrl) {
        if (isValid) {
            isCheckingFavicon = true
            val bmp = NetworkUtils.fetchFavicon(config.targetUrl)
            faviconBitmap = bmp
            isCheckingFavicon = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Column {
            Text(
                text = "Target Web URL & Scope",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Enter the responsive web application, PWA, or portal to compile into a native APK.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // URL Input Field
        Card(
            modifier = Modifier.fillMaxWidth().testTag("target_url_input_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(if (isValid) AccentCyan else BorderSubtle)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "TARGET WEB URL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyanLight,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = config.targetUrl,
                    onValueChange = { input ->
                        val normalized = input.trim()
                        val domain = NetworkUtils.extractDomain(normalized)
                        val suggestedPkg = if (config.packageName.startsWith("com.company") && domain.isNotBlank()) {
                            NetworkUtils.suggestPackageName(domain, config.appName)
                        } else {
                            config.packageName
                        }
                        onConfigChange(config.copy(targetUrl = normalized, packageName = suggestedPkg))
                    },
                    placeholder = { Text("https://mywebapp.com", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "URL",
                            tint = if (isValid) AccentEmerald else TextMuted
                        )
                    },
                    trailingIcon = {
                        if (config.targetUrl.isNotBlank()) {
                            IconButton(onClick = { onConfigChange(config.copy(targetUrl = "https://")) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear URL",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("url_input_field")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick protocol shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("https://", "http://", "localhost:3000").forEach { prefix ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceElevated,
                                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            ) {
                                Text(
                                    text = prefix,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AccentCyanLight,
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (isValid) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Valid URL",
                                tint = AccentEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Valid HTTPS endpoint", fontSize = 11.sp, color = AccentEmerald)
                        }
                    }
                }
            }
        }

        // Live Domain & Favicon Card
        if (isValid) {
            val domain = NetworkUtils.extractDomain(config.targetUrl)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderHighlight))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        if (faviconBitmap != null) {
                            Image(
                                bitmap = faviconBitmap!!.asImageBitmap(),
                                contentDescription = "Website Favicon",
                                modifier = Modifier.size(24.dp)
                            )
                        } else if (isCheckingFavicon) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = AccentCyan)
                        } else {
                            Icon(imageVector = Icons.Default.Public, contentDescription = "Web", tint = AccentCyan, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = domain,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Auto-configured base SSL origin & WebView sandbox",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Domain Whitelist Component
        DomainWhitelistEditor(
            domains = config.whitelistedDomains,
            onDomainsChanged = { updated ->
                onConfigChange(config.copy(whitelistedDomains = updated))
            }
        )

        // Offline Caching Strategy Selector
        Card(
            modifier = Modifier.fillMaxWidth().testTag("caching_policy_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "CACHING & OFFLINE POLICY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyanLight,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                CachingPolicy.values().forEach { policy ->
                    val isSelected = config.cachingPolicy == policy
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryIndigo.copy(alpha = 0.15f) else SurfaceDark,
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) PrimaryIndigo else BorderSubtle)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onConfigChange(config.copy(cachingPolicy = policy)) },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryIndigo, unselectedColor = TextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = policy.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) AccentCyanLight else TextPrimary
                                )
                                Text(
                                    text = policy.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
