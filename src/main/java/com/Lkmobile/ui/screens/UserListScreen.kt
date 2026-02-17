package com.Lkmobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onRefresh: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val otherUsers = state.users.filter { it.id != state.userId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SurfaceDark,
                                BackgroundDark
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Players",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${otherUsers.size} online",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Game status indicator for current user
                            if (state.isGameRunning) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AccentGreen.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        PulsingDot(
                                            color = AccentGreen,
                                            size = 8f
                                        )
                                        Text(
                                            text = "In Game",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AccentGreen
                                        )
                                    }
                                }
                            }

                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Menu",
                                        tint = TextSecondary
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier.background(CardDark)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Launch Mobile Legends",
                                                color = TextPrimary
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            onLaunchGame()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.SportsEsports,
                                                contentDescription = null,
                                                tint = GamingPurple
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text("Refresh", color = TextPrimary)
                                        },
                                        onClick = {
                                            showMenu = false
                                            onRefresh()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = null,
                                                tint = Secondary
                                            )
                                        }
                                    )
                                    HorizontalDivider(color = DividerDark)
                                    DropdownMenuItem(
                                        text = {
                                            Text("Log Out", color = AccentRed)
                                        },
                                        onClick = {
                                            showMenu = false
                                            onLogout()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Logout,
                                                contentDescription = null,
                                                tint = AccentRed
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // My profile card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardDark.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box {
                                UserAvatar(
                                    name = state.userName,
                                    color = 0xFF6C63FF.toInt(),
                                    size = 44.dp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = state.userName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (state.isGameRunning) "Playing Mobile Legends" else "Online",
                                    fontSize = 13.sp,
                                    color = if (state.isGameRunning) AccentGreen else TextSecondary
                                )
                            }
                            Text(
                                text = "You",
                                fontSize = 12.sp,
                                color = TextTertiary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // User list
            if (otherUsers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No other players yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Share your access ID with friends\nso they can join",
                            fontSize = 14.sp,
                            color = TextTertiary,
                            lineHeight = 20.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(otherUsers, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            onInvite = { onInvite(user) },
                            inviteSent = state.inviteSent
                        )
                    }

                    item {
                        Spacer(modifier = Modifier
                            .navigationBarsPadding()
                            .height(16.dp))
                    }
                }
            }
        }

        // Invite sent toast
        AnimatedVisibility(
            visible = state.inviteSent,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AccentGreen.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Invite sent!",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onInvite: () -> Unit,
    inviteSent: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardDark
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box {
                UserAvatar(
                    name = user.name,
                    color = user.avatarColor,
                    size = 50.dp
                )
                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                if (user.isPlaying) AccentGreen else Color(0xFF4CAF50)
                            )
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (user.isPlaying) {
                        PulsingDot(
                            color = AccentGreen,
                            size = 8f
                        )
                        Text(
                            text = "Playing Mobile Legends",
                            fontSize = 13.sp,
                            color = AccentGreen,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (user.isOnline) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Text(
                            text = "Online",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TextTertiary)
                        )
                        Text(
                            text = "Offline",
                            fontSize = 13.sp,
                            color = TextTertiary
                        )
                    }
                }
            }

            // Invite button
            Surface(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onInvite
                    ),
                shape = RoundedCornerShape(12.dp),
                color = Primary.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Invite",
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Invite",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
            }
        }
    }
}
