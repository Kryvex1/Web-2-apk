package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WebPermission
import com.example.ui.theme.*

@Composable
fun PermissionItemCard(
    permission: WebPermission,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBorderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryIndigo else BorderSubtle.copy(alpha = 0.6f),
        label = "perm_border"
    )

    val icon: ImageVector = when (permission) {
        WebPermission.CAMERA -> Icons.Default.CameraAlt
        WebPermission.LOCATION -> Icons.Default.LocationOn
        WebPermission.STORAGE -> Icons.Default.FolderOpen
        WebPermission.MICROPHONE -> Icons.Default.Mic
        WebPermission.NOTIFICATIONS -> Icons.Default.NotificationsActive
        WebPermission.BIOMETRICS -> Icons.Default.Fingerprint
        WebPermission.NETWORK_STATE -> Icons.Default.Wifi
        WebPermission.VIBRATION -> Icons.Default.Vibration
        WebPermission.WAKE_LOCK -> Icons.Default.PowerSettingsNew
        WebPermission.BACKGROUND_AUDIO -> Icons.Default.MusicNote
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle(!isSelected) }
            .testTag("perm_card_${permission.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceCard else SurfaceDark.copy(alpha = 0.7f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(cardBorderColor))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryIndigo.copy(alpha = 0.2f) else SurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = permission.title,
                    tint = if (isSelected) AccentCyanLight else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = permission.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    if (permission.isDefaultBrowser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Standard",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = permission.permissionString,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryIndigo,
                    checkmarkColor = Color.White,
                    uncheckedColor = TextMuted
                ),
                modifier = Modifier.testTag("perm_checkbox_${permission.id}")
            )
        }
    }
}
