package com.Lkmobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.R
import com.Lkmobile.ui.theme.*
import com.Lkmobile.util.LocaleHelper
import com.Lkmobile.util.GameDetector
import com.Lkmobile.viewmodel.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: AppState,
    onToggleMock: (Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenLogs: () -> Unit
) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(LocaleHelper.getLanguage(context)) }
    val hasPermission = GameDetector.hasUsageStatsPermission(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundDark, Color(0xFF0F0F20))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = TextPrimary
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SettingsSection(title = "App Preferences") {
                    LanguageSelector(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { code: String ->
                            LocaleHelper.setLanguage(context, code)
                            currentLanguage = code
                        }
                    )
                }

                SettingsSection(title = "System & Debug") {
                    StatusItem(
                        icon = Icons.Default.Security,
                        title = "Usage Access",
                        subtitle = "Required for game detection",
                        enabled = hasPermission,
                        onToggle = { GameDetector.requestUsageStatsPermission(context) }
                    )
                    HorizontalDivider(color = DividerDark, modifier = Modifier.padding(horizontal = 20.dp))
                    StatusItem(
                        icon = Icons.Default.BugReport,
                        title = "Mock Mode",
                        subtitle = "Use fake server data",
                        enabled = state.useMockMode,
                        onToggle = onToggleMock
                    )
                    HorizontalDivider(color = DividerDark, modifier = Modifier.padding(horizontal = 20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenLogs() }
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Secondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.List, null, tint = Secondary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("System Logs", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("View application events", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    "v2.5.0-PREMIUM",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextTertiary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
fun LanguageSelector(currentLanguage: String, onLanguageSelected: (String) -> Unit) {
    // Placeholder for LanguageSelector if not defined elsewhere
    Text("Language Selector Placeholder", color = TextPrimary)
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = Primary,
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardDark,
            border = BorderStroke(1.dp, DividerDark)
        ) {
            Column { content() }
        }
    }
}

@Composable
fun StatusItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!enabled) }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) Primary.copy(alpha = 0.2f) else DividerDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon, 
                null, 
                tint = if (enabled) Primary else TextSecondary, 
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary,
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = DividerDark
            )
        )
    }
}
