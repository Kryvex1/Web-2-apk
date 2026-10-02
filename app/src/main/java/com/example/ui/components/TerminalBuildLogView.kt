package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.builder.BuildLogEntry
import com.example.builder.LogLevel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TerminalBuildLogView(
    logs: List<BuildLogEntry>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("terminal_build_log_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Terminal Titlebar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TerminalRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TerminalYellow))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TerminalGreen))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "BUILD_ENGINE_OUTPUT ~ /aapt2/dx/signer",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "${logs.size} EVENTS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = AccentCyanLight
                )
            }

            // Log Console Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = 260.dp)
                    .padding(10.dp)
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "> Ready to compile. Waiting for build pipeline trigger...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                } else {
                    items(logs) { entry ->
                        val levelColor = when (entry.level) {
                            LogLevel.INFO -> AccentCyanLight
                            LogLevel.DEBUG -> TextMuted
                            LogLevel.COMPILER -> PrimaryIndigoLight
                            LogLevel.SUCCESS -> TerminalGreen
                            LogLevel.WARNING -> TerminalYellow
                            LogLevel.ERROR -> TerminalRed
                        }

                        val timeStr = timeFormat.format(Date(entry.timestamp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "[$timeStr]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "[${entry.level.name}]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = levelColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = entry.message,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = if (entry.level == LogLevel.SUCCESS) TerminalGreen else TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
