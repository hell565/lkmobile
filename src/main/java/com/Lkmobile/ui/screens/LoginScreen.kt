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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val focusRequester = remember { FocusRequester() }

    val logoScale by rememberInfiniteTransition(label = "logo").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        SurfaceDark,
                        BackgroundDark
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-50).dp)
                .clip(CircleShape)
                .background(
                    Primary.copy(alpha = 0.05f)
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 80.dp)
                .clip(CircleShape)
                .background(
                    Secondary.copy(alpha = 0.05f)
                )
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
            Spacer(modifier = Modifier.weight(0.8f))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(logoScale)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Primary, Secondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "LK Mobile",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your access ID to continue",
                fontSize = 15.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = accessId,
                onValueChange = {
                    accessId = it
                    if (state.error != null) onClearError()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Access ID") },
                placeholder = { Text("Enter your ID") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = Primary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showId = !showId }) {
                        Icon(
                            imageVector = if (showId) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showId) "Hide" else "Show",
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (showId) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (accessId.isNotBlank()) {
                            onVerifyId(accessId.trim())
                        }
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = DividerDark,
                    cursorColor = Primary,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardDark.copy(alpha = 0.5f),
                    unfocusedContainerColor = CardDark.copy(alpha = 0.3f)
                ),
                isError = state.error != null
            )

            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = state.error ?: "",
                    color = AccentRed,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            GradientButton(
                text = "Verify & Enter",
                onClick = {
                    focusManager.clearFocus()
                    onVerifyId(accessId.trim())
                },
                enabled = accessId.isNotBlank(),
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "v1.0",
                fontSize = 12.sp,
                color = TextTertiary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
