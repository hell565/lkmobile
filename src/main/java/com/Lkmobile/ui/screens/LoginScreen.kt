package com.Lkmobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.R
import com.Lkmobile.ui.components.GradientButton
import com.Lkmobile.ui.theme.*
import com.Lkmobile.viewmodel.AppState

@Composable
fun LoginScreen(
    state: AppState,
    onVerifyId: (String) -> Unit,
    onClearError: () -> Unit
) {
    var accessId by remember { mutableStateOf("") }
    var showId by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Aesthetic Glows
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .scale(glowScale)
                .background(Brush.radialGradient(listOf(Primary.copy(alpha = 0.12f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .scale(glowScale * 0.8f)
                .background(Brush.radialGradient(listOf(Secondary.copy(alpha = 0.12f), Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(30.dp, RoundedCornerShape(28.dp), spotColor = Primary),
                shape = RoundedCornerShape(28.dp),
                color = SurfaceDark
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(Primary, GamingPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Key, null, tint = Color.White, modifier = Modifier.size(44.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "LK MOBILE",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = AccentGreen,
                letterSpacing = 4.sp,
                modifier = Modifier.shadow(30.dp, spotColor = AccentGreen)
            )

            Text(
                text = "PREMIUM LAUNCHER EDITION",
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = accessId,
                onValueChange = {
                    accessId = it
                    if (state.error != null) onClearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.id_hint)) },
                placeholder = { Text("E.g. SQUAD-123") },
                leadingIcon = { Icon(Icons.Default.Key, null, tint = Primary) },
                trailingIcon = {
                    IconButton(onClick = { showId = !showId }) {
                        Icon(
                            imageVector = if (showId) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (showId) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (accessId.isNotBlank()) onVerifyId(accessId.trim())
                }),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = DividerDark,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark.copy(alpha = 0.5f),
                    focusedLabelColor = Primary,
                    cursorColor = Primary
                ),
                isError = state.error != null
            )

            AnimatedVisibility(visible = state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = AccentRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            GradientButton(
                text = stringResource(R.string.verify_button),
                onClick = {
                    focusManager.clearFocus()
                    onVerifyId(accessId.trim())
                },
                enabled = accessId.isNotBlank(),
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "ULTIMATE EDITION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextTertiary,
                modifier = Modifier.padding(bottom = 24.dp),
                letterSpacing = 1.sp
            )
        }
    }
}
