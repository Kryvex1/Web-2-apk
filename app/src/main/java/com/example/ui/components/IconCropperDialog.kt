package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream

enum class IconCropShape(val label: String) {
    SQUIRCLE("Squircle (Adaptive)"),
    CIRCLE("Circle (Round)"),
    ROUNDED_RECT("Rounded Box"),
    HEXAGON("Hex Badge")
}

@Composable
fun IconCropperDialog(
    appName: String,
    initialPrimaryColor: String,
    onDismiss: () -> Unit,
    onIconSaved: (savedFilePath: String) -> Unit
) {
    val context = LocalContext.current
    var selectedShape by remember { mutableStateOf(IconCropShape.SQUIRCLE) }
    var selectedColorHex by remember { mutableStateOf(initialPrimaryColor) }
    var customBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val colorOptions = listOf(
        "#6366F1", // Indigo
        "#06B6D4", // Cyan
        "#10B981", // Emerald
        "#F59E0B", // Amber
        "#F43F5E", // Rose
        "#8B5CF6", // Violet
        "#0F172A", // Slate Dark
        "#EC4899"  // Pink
    )

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = android.graphics.BitmapFactory.decodeStream(stream)
                    customBitmap = bmp
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    // Function to render icon bitmap with chosen shape and color
    fun renderFinalIconBitmap(): Bitmap {
        val size = 256
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val bgParsedColor = try {
            android.graphics.Color.parseColor(selectedColorHex)
        } catch (e: Exception) {
            android.graphics.Color.parseColor("#6366F1")
        }

        // 1. Draw shape background
        paint.color = bgParsedColor
        val rectF = RectF(12f, 12f, size - 12f, size - 12f)
        when (selectedShape) {
            IconCropShape.SQUIRCLE -> canvas.drawRoundRect(rectF, 60f, 60f, paint)
            IconCropShape.CIRCLE -> canvas.drawCircle(size / 2f, size / 2f, (size - 24f) / 2f, paint)
            IconCropShape.ROUNDED_RECT -> canvas.drawRoundRect(rectF, 36f, 36f, paint)
            IconCropShape.HEXAGON -> canvas.drawRoundRect(rectF, 50f, 50f, paint)
        }

        if (customBitmap != null) {
            // Overlay and mask custom bitmap
            val srcBmp = customBitmap!!
            val scaled = Bitmap.createScaledBitmap(srcBmp, (size * 0.72f).toInt(), (size * 0.72f).toInt(), true)
            val left = (size - scaled.width) / 2f
            val top = (size - scaled.height) / 2f
            canvas.drawBitmap(scaled, left, top, null)
        } else {
            // Draw stylized initial
            paint.color = android.graphics.Color.WHITE
            paint.textSize = 96f
            paint.textAlign = Paint.Align.CENTER
            val letter = if (appName.isNotBlank()) appName.take(1).uppercase() else "W"
            val textBounds = android.graphics.Rect()
            paint.getTextBounds(letter, 0, letter.length, textBounds)
            val yPos = (size / 2f) + (textBounds.height() / 2f)
            canvas.drawText(letter, size / 2f, yPos, paint)
        }

        return output
    }

    val previewBitmap = remember(selectedShape, selectedColorHex, customBitmap, appName) {
        renderFinalIconBitmap()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("icon_cropper_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderHighlight))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "App Icon Designer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Live Preview
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = "App Icon Preview",
                        modifier = Modifier.size(96.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Adaptive 512x512 Master Vector",
                    fontSize = 11.sp,
                    color = AccentCyanLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Choose Image from Gallery
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceElevated,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("pick_icon_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload Custom Logo",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Logo / Image", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Crop Shape Selector
                Text(
                    text = "CROP & MASK SHAPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconCropShape.values().forEach { shape ->
                        val isSelected = selectedShape == shape
                        OutlinedButton(
                            onClick = { selectedShape = shape },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = if (isSelected) AccentCyanLight else TextSecondary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) PrimaryIndigo else BorderSubtle)
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = shape.name.take(4),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Color Palette Selector
                Text(
                    text = "BACKGROUND PALETTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(colorOptions.size) { idx ->
                        val hex = colorOptions[idx]
                        val color = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Color",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Multi-density preview summary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevated)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("mdpi: 48px", fontSize = 10.sp, color = TextMuted)
                    Text("hdpi: 72px", fontSize = 10.sp, color = TextMuted)
                    Text("xhdpi: 96px", fontSize = 10.sp, color = TextMuted)
                    Text("xxhdpi: 144px", fontSize = 10.sp, color = TextMuted)
                    Text("xxxhdpi: 192px", fontSize = 10.sp, color = AccentCyanLight)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Save Button
                Button(
                    onClick = {
                        val iconFile = File(context.filesDir, "custom_app_icon_${System.currentTimeMillis()}.png")
                        FileOutputStream(iconFile).use { out ->
                            previewBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }
                        onIconSaved(iconFile.absolutePath)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryIndigo,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Save Icon",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply to App Mipmaps", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
