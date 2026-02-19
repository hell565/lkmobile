package com.Lkmobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
            .background(Brush.verticalGradient(listOf(BackgroundDark, Color(0xFF101025))))
    ) {
        // Декоративные элементы фона
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 200.dp, y = (-100).dp)
                .background(Brush.radialGradient(listOf(Primary.copy(alpha = 0.1f), Color.Transparent)))
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
            Spacer(modifier = Modifier.weight(0.4f))

            // Аватар с неоновым свечением
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(30.dp, CircleShape, spotColor = selectedColor, ambientColor = selectedColor),
                shape = CircleShape,
                color = SurfaceDark,
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(selectedColor, Color.Transparent)))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(selectedColor.copy(alpha = 0.8f), selectedColor.copy(alpha = 0.2f)))),
                    contentAlignment = Alignment.Center
                ) {
                    if (name.isNotBlank()) {
                        Text(
                            text = name.first().uppercase(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 64.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                    } else {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(70.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "LK MOBILE",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = AccentGreen,
                letterSpacing = 4.sp,
                modifier = Modifier.shadow(30.dp, spotColor = AccentGreen)
            )

            Text(
                text = "ESTABLISH IDENTITY",
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(56.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 15) {
                        name = it
                        if (state.error != null) onClearError()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                placeholder = { Text("Enter your nickname...", color = TextTertiary) },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = selectedColor) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (name.isNotBlank()) onRegisterName(name.trim())
                }),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = selectedColor,
                    unfocusedBorderColor = DividerDark,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark,
                    cursorColor = selectedColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                supportingText = {
                    Text(
                        "${name.length}/15", 
                        color = if (name.length >= 15) AccentRed else TextTertiary, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
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
