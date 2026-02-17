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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.R
import com.Lkmobile.ui.theme.*
import com.Lkmobile.util.LocaleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(LocaleHelper.getLanguage(context)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings).uppercase(), fontWeight = FontWeight.Black, letterSpacing = 1.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, null, tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            SettingsSection(title = "App Preferences") {
                LanguageSelector(
                    currentLanguage = currentLanguage,
                    onLanguageSelected = { code ->
                        LocaleHelper.setLanguage(context, code)
                        currentLanguage = code
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Game Tracking") {
                StatusItem(
                    icon = Icons.Default.AutoGraph,
                    title = "Precise Tracking",
                    subtitle = "Enhanced background game detection",
                    enabled = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "v2.1.0-ULTIMATE",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = TextTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
            letterSpacing = 1.sp
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, DividerDark)
        ) {
            Column { content() }
        }
    }
}

@Composable
fun LanguageSelector(currentLanguage: String, onLanguageSelected: (String) -> Unit) {
    Column {
        LanguageRow("ENGLISH", "en", currentLanguage == "en") { onLanguageSelected("en") }
        HorizontalDivider(color = DividerDark, modifier = Modifier.padding(horizontal = 20.dp))
        LanguageRow("РУССКИЙ", "ru", currentLanguage == "ru") { onLanguageSelected("ru") }
    }
}

@Composable
fun LanguageRow(name: String, code: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Primary.copy(alpha = 0.2f) else DividerDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Language,
                    null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) Primary else TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontWeight = FontWeight.Bold, color = if (isSelected) TextPrimary else TextSecondary)
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Secondary)
        )
    }
}

@Composable
fun StatusItem(icon: ImageVector, title: String, subtitle: String, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(AccentGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = AccentGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = TextTertiary)
        }
        Switch(
            checked = enabled,
            onCheckedChange = {},
            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = AccentGreen.copy(alpha = 0.3f))
        )
    }
}
