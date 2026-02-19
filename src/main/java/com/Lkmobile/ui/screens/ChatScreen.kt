package com.Lkmobile.ui.screens

import androidx.compose.ui.res.stringResource
import com.Lkmobile.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.ui.components.UserAvatar
import androidx.compose.material.icons.filled.DoneAll
import com.Lkmobile.viewmodel.AppState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: AppState,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.chatMessages.size) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Chat Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    Text(
                        "GLOBAL CHAT",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Primary,
                            letterSpacing = 2.sp
                        )
                    )
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
            ) {
                items(state.chatMessages, key = { it.id }) { message ->
                    val isMe = message.from == state.userName
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        if (!isMe) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UserAvatar(name = message.from, color = message.color, size = 32.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    message.from,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(message.color)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        
                        Surface(
                            color = if (isMe) Primary.copy(alpha = 0.2f) else CardDark,
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = if (isMe) 20.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 20.dp
                            ),
                            border = BorderStroke(1.dp, if (isMe) Primary.copy(alpha = 0.6f) else DividerDark),
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                                .shadow(if (isMe) 8.dp else 2.dp, RoundedCornerShape(20.dp), spotColor = if (isMe) Primary else Color.Black)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    message.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        timeFormat.format(Date(message.time)),
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary)
                                    )
                                    if (isMe) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.DoneAll,
                                            null,
                                            modifier = Modifier.size(14.dp),
                                            tint = AccentGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input Field Floating
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = SurfaceDark.copy(alpha = 0.95f),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, DividerDark),
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(id = R.string.chat_hint), color = TextTertiary) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Primary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4
                )
                
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendMessage(text)
                            text = ""
                        }
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .size(48.dp)
                        .background(Primary, CircleShape)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
