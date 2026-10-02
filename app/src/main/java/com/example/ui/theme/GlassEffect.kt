package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Clean glassmorphic card modifier with crystal crisp child content,
 * subtle vertical gradient surface, and luminous borders.
 */
fun Modifier.glassmorphic(
    backgroundColor: Color = SurfaceCard,
    borderColor: Color = BorderSubtle.copy(alpha = 0.6f),
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    this
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    backgroundColor.copy(alpha = 0.92f),
                    backgroundColor.copy(alpha = 0.80f)
                )
            ),
            shape = shape
        )
        .border(
            width = borderWidth,
            brush = Brush.verticalGradient(
                colors = listOf(
                    borderColor.copy(alpha = 0.7f),
                    borderColor.copy(alpha = 0.25f)
                )
            ),
            shape = shape
        )
}

/**
 * Top Camera Cutout / Notch Fade-Blur Gradient Header.
 * Dark & protective around the status bar / camera notch, fading smoothly downwards
 * so scrolling content naturally glides behind the notch.
 */
fun Modifier.notchFadeHeader(
    baseColor: Color = BackgroundDark
): Modifier = composed {
    this.background(
        brush = Brush.verticalGradient(
            colors = listOf(
                baseColor.copy(alpha = 0.98f), // Solid under camera cutout/notch
                baseColor.copy(alpha = 0.90f), // Soft translucent transition
                baseColor.copy(alpha = 0.75f), // Translucent backdrop
                baseColor.copy(alpha = 0.0f)   // Smooth fade into content
            )
        )
    )
}

/**
 * Clean Floating Bottom Dock Bar.
 */
fun Modifier.dockBarSurface(
    backgroundColor: Color = BackgroundDark.copy(alpha = 0.92f),
    borderColor: Color = BorderSubtle.copy(alpha = 0.5f)
): Modifier = composed {
    this
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    backgroundColor.copy(alpha = 0.88f),
                    backgroundColor.copy(alpha = 0.96f)
                )
            )
        )
        .border(
            width = 0.5.dp,
            color = borderColor
        )
}
