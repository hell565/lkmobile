package com.Lkmobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.ui.theme.*
import com.Lkmobile.viewmodel.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    state: AppState,
    onCreateLobby: (String) -> Unit,
    onJoinLobby: (String) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var lobbyName by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "LOBBY LIST", 
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black, 
                                color = Primary,
                                letterSpacing = 2.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(40.dp)
                                .background(Brush.horizontalGradient(listOf(Primary, Secondary)), RoundedCornerShape(1.dp))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundDark)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true }, 
                containerColor = Primary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null, tint = Color.White) },
                text = { Text("NEW LOBBY", fontWeight = FontWeight.Bold, color = Color.White) }
            )
        }
    ) { padding ->
        if (state.lobbies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Primary.copy(alpha = 0.05f), RoundedCornerShape(30.dp))
                            .border(1.dp, Primary.copy(alpha = 0.1f), RoundedCornerShape(30.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Group, null, tint = Primary.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("No active lobbies", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Be the first to start a battle!", color = TextTertiary, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.lobbies, key = { it.id }) { lobby ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onJoinLobby(lobby.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = CardDark,
                        border = BorderStroke(1.dp, DividerDark.copy(alpha = 0.5f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.linearGradient(listOf(Primary.copy(alpha = 0.2f), Secondary.copy(alpha = 0.1f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Group, null, tint = Primary, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    lobby.name.uppercase(), 
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold, 
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).background(AccentGreen, RoundedCornerShape(3.dp)))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Host: ${lobby.creator}", color = TextTertiary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Surface(
                                color = Primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${lobby.memberCount}", color = Primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Group, null, tint = Primary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = SurfaceDark,
                title = { Text("Create New Lobby", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    TextField(
                        value = lobbyName,
                        onValueChange = { if (it.length <= 15) lobbyName = it },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                        placeholder = { Text("Enter lobby name", color = TextTertiary) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CardDark,
                            unfocusedContainerColor = CardDark,
                            focusedIndicatorColor = Primary,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (lobbyName.isNotBlank()) {
                                onCreateLobby(lobbyName)
                                showCreateDialog = false
                                lobbyName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) { Text("Create", fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancel", color = TextTertiary) }
                }
            )
        }
    }
}
