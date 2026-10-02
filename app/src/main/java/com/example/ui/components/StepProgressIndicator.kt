package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int = 4,
    onStepClick: (Int) -> Unit = {}
) {
    val stepTitles = listOf("URL & Scope", "Permissions", "Identity & Icon", "UI & Splash")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("step_progress_indicator")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in 1..totalSteps) {
                val isCompleted = step < currentStep
                val isCurrent = step == currentStep
                val isFuture = step > currentStep

                val circleBg by animateColorAsState(
                    targetValue = when {
                        isCompleted -> AccentEmerald
                        isCurrent -> PrimaryIndigo
                        else -> SurfaceElevated
                    },
                    animationSpec = tween(300),
                    label = "step_bg"
                )

                val contentColor by animateColorAsState(
                    targetValue = when {
                        isCompleted || isCurrent -> Color.White
                        else -> TextMuted
                    },
                    label = "step_text_color"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = (step < totalSteps))
                ) {
                    // Step Circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(circleBg)
                            .clickable(enabled = step <= currentStep + 1) {
                                onStepClick(step)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed Step $step",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "$step",
                                color = contentColor,
                                fontSize = 13.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    // Connecting Line
                    if (step < totalSteps) {
                        val lineProgress by animateFloatAsState(
                            targetValue = if (currentStep > step) 1f else 0f,
                            animationSpec = tween(400),
                            label = "line_anim"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .padding(horizontal = 6.dp)
                                .clip(CircleShape)
                                .background(SurfaceElevated)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(lineProgress)
                                    .background(PrimaryIndigo)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Step Title label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val title = stepTitles.getOrElse(currentStep - 1) { "Configuration" }
            Text(
                text = "STAGE $currentStep OF $totalSteps: $title".uppercase(),
                color = AccentCyanLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "${(currentStep * 25)}% Complete",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
