package com.Lkmobile.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.R
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
            .background(BackgroundDark)
    ) {
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
                    .size(120.dp)
                    .shadow(25.dp, CircleShape, spotColor = selectedColor),
                shape = CircleShape,
                color = SurfaceDark
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(selectedColor, selectedColor.copy(alpha = 0.5f)))),
                    contentAlignment = Alignment.Center
                ) {
                    if (name.isNotBlank()) {
                        Text(
                            text = name.first().uppercase(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(56.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.name_title).uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.name_subtitle),
                fontSize = 15.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
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
                label = { Text(stringResource(R.string.name_hint)) },
                placeholder = { Text("Enter nickname") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = selectedColor) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (name.isNotBlank()) onRegisterName(name.trim())
                }),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = selectedColor,
                    unfocusedBorderColor = DividerDark,
                    focusedContainerColor = SurfaceDark,
                    cursorColor = selectedColor,
                    focusedLabelColor = selectedColor
                ),
                supportingText = {
                    Text("${name.length}/20", color = TextTertiary, fontWeight = FontWeight.Bold)
                },
                isError = state.error != null
            )

            AnimatedVisibility(visible = state.error != null) {
                Text(state.error ?: "", color = AccentRed, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            GradientButton(
                text = stringResource(R.string.continue_button),
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
