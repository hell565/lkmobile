package com.Lkmobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.automirrored.outlined.SportsEsports
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Lkmobile.R
import com.Lkmobile.model.User
import com.Lkmobile.ui.components.PulsingDot
import com.Lkmobile.ui.components.UserAvatar
import com.Lkmobile.ui.theme.*
import com.Lkmobile.viewmodel.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    state: AppState,
    onInvite: (User) -> Unit,
    onLaunchGame: () -> Unit,
    onLogout: () -> Unit,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenLobby: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val otherUsers = state.users.filter { it.id != state.userId }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onLaunchGame,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(64.dp)
                    .padding(bottom = 8.dp)
                    .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = AccentGreen),
                containerColor = AccentGreen,
                contentColor = Color.Black,
                shape = RoundedCornerShape(32.dp),
                icon = { Icon(Icons.Default.SportsEsports, null, modifier = Modifier.size(28.dp)) },
                text = { 
                    Text(
                        stringResource(id = R.string.launch_game).uppercase(), 
                        fontWeight = FontWeight.Black, 
                        letterSpacing = 2.sp,
                        fontSize = 16.sp
                    ) 
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .offset(x = (-150).dp, y = (-100).dp)
                    .background(Brush.radialGradient(listOf(Primary.copy(alpha = 0.15f), Color.Transparent)))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                            Text(
                                stringResource(id = R.string.users_title).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 2.sp
                            )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE SESSION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentGreen.copy(alpha = 0.1f))
                                .border(1.dp, AccentGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = AccentGreen)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .border(1.dp, DividerDark, RoundedCornerShape(12.dp))
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextPrimary)
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier
                                    .background(CardDarkElevated)
                                    .border(1.dp, DividerDark, RoundedCornerShape(12.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.settings), color = TextPrimary) },
                                    onClick = { showMenu = false; onOpenSettings() },
                                    leadingIcon = { Icon(Icons.Default.Settings, null, tint = Primary) }
                                )
                                HorizontalDivider(color = DividerDark)
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.logout), color = AccentRed) },
                                    onClick = { showMenu = false; onLogout() },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = AccentRed) }
                                )
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(30.dp, RoundedCornerShape(28.dp), spotColor = AccentGreen, ambientColor = AccentGreen),
                    shape = RoundedCornerShape(28.dp),
                    color = CardDark,
                    border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(AccentGreen.copy(alpha = 0.5f), Color.Transparent)))
                ) {
                    Row(
                        modifier = Modifier
                            .background(Brush.horizontalGradient(listOf(AccentGreen.copy(alpha = 0.1f), Color.Transparent)))
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            UserAvatar(name = state.userName, color = Primary.toArgb(), size = 64.dp)
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen)
                                    .border(3.dp, CardDark, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                state.userName.uppercase(), 
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black, 
                                    color = TextPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.isGameRunning) {
                                Box(modifier = Modifier.size(8.dp).background(AccentGreen, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    stringResource(id = R.string.status_playing).uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = AccentGreen
                                    )
                                )
                            } else {
                                Box(modifier = Modifier.size(8.dp).background(Secondary, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "STATUS: ${stringResource(id = R.string.status_online).uppercase()}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                )
                            }
                        }
                        }
                        if (state.isGameRunning) {
                            PulsingDot(color = AccentGreen, size = 12f)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (otherUsers.isEmpty()) {
                    EmptyStateView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(otherUsers, key = { it.id }) { user ->
                            UserCard(
                                user = user, 
                                onInvite = { onInvite(user) },
                                onPrivateChat = { /* Navigate to private chat logic */ }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(110.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onInvite: () -> Unit, onPrivateChat: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPrivateChat() }
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(24.dp),
        color = CardDark,
        border = BorderStroke(1.dp, Brush.linearGradient(listOf(DividerDark, Color.Transparent)))
    ) {
        Row(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.03f), Color.Transparent)))
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                UserAvatar(name = user.name, color = user.avatarColor, size = 56.dp)
                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (user.isPlaying) AccentGreen else Color(0xFF4CAF50))
                            .border(2.5.dp, CardDark, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.name, 
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold, 
                        color = TextPrimary
                    )
                )
                            val statusText = when {
                                user.isPlaying -> stringResource(id = R.string.status_playing)
                                user.isOnline -> stringResource(id = R.string.status_online)
                                else -> stringResource(id = R.string.status_offline)
                            }
                            val statusColor = when {
                                user.isPlaying -> AccentRed
                                user.isOnline -> AccentGreen
                                else -> TextTertiary
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(statusColor, CircleShape)
                                        .shadow(if (user.isOnline) 4.dp else 0.dp, CircleShape, spotColor = statusColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    statusText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (user.isPlaying || user.isOnline) TextSecondary else TextTertiary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
            }
            
            IconButton(
                onClick = onInvite,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Primary.copy(alpha = 0.15f))
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = Primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.AutoMirrored.Outlined.SportsEsports, null, modifier = Modifier.size(80.dp), tint = TextTertiary.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(24.dp))
        Text("STILL WAITING...", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Share your ID to start building your squad", textAlign = TextAlign.Center, color = TextTertiary)
    }
}