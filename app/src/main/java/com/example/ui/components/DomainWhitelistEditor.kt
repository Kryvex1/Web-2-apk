package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DomainWhitelistEditor(
    domains: List<String>,
    onDomainsChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var newDomainInput by remember { mutableStateOf("") }
    var isInputExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("domain_whitelist_card"),
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
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Domain Scope",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Domain Whitelisting & Scope",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                TextButton(
                    onClick = { isInputExpanded = !isInputExpanded },
                    modifier = Modifier.testTag("toggle_add_domain_btn")
                ) {
                    Icon(
                        imageVector = if (isInputExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Domain",
                        modifier = Modifier.size(16.dp),
                        tint = AccentCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isInputExpanded) "Cancel" else "Add Domain",
                        fontSize = 12.sp,
                        color = AccentCyan
                    )
                }
            }

            Text(
                text = "Restricts the generated APK to keep navigation internal. External URLs open in system browser.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isInputExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newDomainInput,
                        onValueChange = { newDomainInput = it },
                        placeholder = { Text("e.g. auth.example.com", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("domain_input_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentCyan
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val clean = newDomainInput.trim().replace(Regex("^https?://"), "").replace(Regex("/.*$"), "")
                            if (clean.isNotBlank() && !domains.contains(clean)) {
                                onDomainsChanged(domains + clean)
                                newDomainInput = ""
                                isInputExpanded = false
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        modifier = Modifier.testTag("confirm_add_domain_btn")
                    ) {
                        Text("Add", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Chips List
            if (domains.isEmpty()) {
                Text(
                    text = "• Auto-scoped to primary target URL host",
                    fontSize = 11.sp,
                    color = AccentEmerald,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(domains) { domain ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SurfaceElevated,
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(BorderHighlight)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = domain,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { onDomainsChanged(domains - domain) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove $domain",
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
