package com.Lkmobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.ui.theme.*
import com.Lkmobile.viewmodel.AppState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    state: AppState,
    onSendMessage: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Chat Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceDark,
            shadowElevation = 8.dp
        ) {
            Text(
                "GLOBAL CHAT",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Primary
            )
        }

        // Messages List
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(state.chatMessages) { message ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            message.from,
                            fontWeight = FontWeight.Bold,
                            color = Color(message.color),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            timeFormat.format(Date(message.time)),
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                    Surface(
                        color = CardDark,
                        shape = RoundedCornerShape(0.dp, 12.dp, 12.dp, 12.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            message.text,
                            modifier = Modifier.padding(12.dp),
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Input Field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceDark,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...", color = TextTertiary) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Primary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendMessage(text)
                            text = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Primary)
                ) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
    }
}
