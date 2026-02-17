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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.ui.components.GradientButton
import com.Lkmobile.ui.theme.*
import com.Lkmobile.viewmodel.AppState

@Composable
fun NameEntryScreen(
    state: AppState,
    onRegisterName: (String) -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val avatarColors = listOf(Primary, Secondary, GamingPurple, AccentGreen, AccentOrange)
    val selectedColor = remember { avatarColors.random() }

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
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(GamingPurple.copy(alpha = 0.06f))
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
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(selectedColor, selectedColor.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (name.isNotBlank()) {
                    Text(
                        text = name.first().uppercase(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Choose Your Name",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This is how others will see you",
                fontSize = 15.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 20) {
                        name = it
                        if (state.error != null) onClearError()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nickname") },
                placeholder = { Text("Enter your nickname") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = selectedColor
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (name.isNotBlank()) {
                            onRegisterName(name.trim())
                        }
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = selectedColor,
                    unfocusedBorderColor = DividerDark,
                    cursorColor = selectedColor,
                    focusedLabelColor = selectedColor,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardDark.copy(alpha = 0.5f),
                    unfocusedContainerColor = CardDark.copy(alpha = 0.3f)
                ),
                supportingText = {
                    Text(
                        text = "${name.length}/20",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                },
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
                        .padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            GradientButton(
                text = "Continue",
                onClick = {
                    focusManager.clearFocus()
                    onRegisterName(name.trim())
                },
                enabled = name.isNotBlank(),
                isLoading = state.isLoading,
                gradientColors = listOf(selectedColor, selectedColor.copy(alpha = 0.7f))
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
