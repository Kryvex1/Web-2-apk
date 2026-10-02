package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SplashStyle
import com.example.ui.theme.*

@Composable
fun LiveSplashPreview(
    appName: String,
    splashStyle: SplashStyle,
    splashBgColorHex: String,
    primaryColorHex: String,
    modifier: Modifier = Modifier
) {
    var keyTrigger by remember { mutableStateOf(0) }

    val bgColor = remember(splashBgColorHex) {
        try {
            Color(android.graphics.Color.parseColor(splashBgColorHex))
        } catch (e: Exception) {
            BackgroundDark
        }
    }

    val primaryColor = remember(primaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(primaryColorHex))
        } catch (e: Exception) {
            PrimaryIndigo
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_splash_preview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BorderSubtle, PrimaryIndigoDark)))
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
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AccentEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE SPLASH PREVIEW (STANDALONE APP)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyanLight,
                        letterSpacing = 0.5.sp
                    )
                }

                IconButton(
                    onClick = { keyTrigger++ },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Replay Splash Animation",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Simulated Device Screen Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                key(keyTrigger, splashStyle) {
                    when (splashStyle) {
                        SplashStyle.LOGO_PULSE -> {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 0.85f,
                                targetValue = 1.15f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(900, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.5f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(900, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "alpha"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .scale(scale)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(primaryColor.copy(alpha = alpha))
                                        .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (appName.isNotBlank()) appName.take(1).uppercase() else "W",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = appName.ifBlank { "My Web App" },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        SplashStyle.SHIMMER_SKELETON -> {
                            val transition = rememberInfiniteTransition(label = "shimmer")
                            val translateAnim by transition.animateFloat(
                                initialValue = -200f,
                                targetValue = 200f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "shimmer_anim"
                            )
                            val brush = Brush.horizontalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.8f),
                                    primaryColor.copy(alpha = 0.3f)
                                ),
                                startX = translateAnim - 100f,
                                endX = translateAnim + 100f
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(brush)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(brush)
                                )
                            }
                        }

                        SplashStyle.MODERN_SPINNER -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(44.dp),
                                    color = primaryColor,
                                    strokeWidth = 3.5.dp,
                                    trackColor = primaryColor.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Loading ${appName.ifBlank { "Application" }}...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        SplashStyle.TITLE_GLOW -> {
                            val infiniteTransition = rememberInfiniteTransition(label = "glow")
                            val glowAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.4f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = EaseInOutSine),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "glow"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = appName.ifBlank { "My Web App" },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor.copy(alpha = glowAlpha),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "STANDALONE NATIVE ENGINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentCyan.copy(alpha = 0.8f),
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }

                        SplashStyle.PROGRESS_BAR -> {
                            val progressAnim = remember { Animatable(0f) }
                            LaunchedEffect(keyTrigger) {
                                progressAnim.snapTo(0f)
                                progressAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(1600, easing = LinearOutSlowInEasing)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                            ) {
                                Text(
                                    text = appName.ifBlank { "My Web App" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                LinearProgressIndicator(
                                    progress = { progressAnim.value },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = primaryColor,
                                    trackColor = primaryColor.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${(progressAnim.value * 100).toInt()}% Initialized",
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
