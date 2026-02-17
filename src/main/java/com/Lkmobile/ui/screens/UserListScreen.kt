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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
    onOpenSettings: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val otherUsers = state.users.filter { it.id != state.userId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Aesthetic background glow
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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.users_title),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = (-1).sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentGreen))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${otherUsers.size} ${stringResource(R.string.online).lowercase()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Secondary)
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurfaceDark)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextPrimary)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(CardDarkElevated).border(1.dp, DividerDark, RoundedCornerShape(12.dp))
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
                                        leadingIcon = { Icon(Icons.Default.Logout, null, tint = AccentRed) }
                                    )
                        }
                    }
                }
            }

            // My Profile (Floating Glass Card)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(20.dp, RoundedCornerShape(24.dp), ambientColor = Primary, spotColor = Primary),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceDark.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Brush.linearGradient(listOf(DividerDark, Color.Transparent)))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        UserAvatar(name = state.userName, color = Primary.toArgb(), size = 56.dp)
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(AccentGreen)
                                .border(2.dp, SurfaceDark, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(state.userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            if (state.isGameRunning) stringResource(R.string.playing_now) else stringResource(R.string.online),
                            fontSize = 13.sp,
                            color = if (state.isGameRunning) AccentGreen else TextSecondary
                        )
                    }
                    if (state.isGameRunning) {
                        PulsingDot(color = AccentGreen, size = 10f)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Users List
            if (otherUsers.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(otherUsers, key = { it.id }) { user ->
                        UserCard(user = user, onInvite = { onInvite(user) })
                    }
                    item { Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp)) }
                }
            }
        }
        
        // Quick Launch Button
        ExtendedFloatingActionButton(
            onClick = onLaunchGame,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .shadow(15.dp, CircleShape, spotColor = GamingPurple),
            containerColor = GamingPurple,
            contentColor = Color.White,
            shape = CircleShape,
            icon = { Icon(Icons.Default.SportsEsports, null) },
            text = { Text("LAUNCH GAME", fontWeight = FontWeight.Black) }
        )

        // Invite Confirmation
        AnimatedVisibility(
            visible = state.inviteSent,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AccentGreen,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("INVITE SENT SUCCESSFULLY!", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onInvite: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardDark,
        border = BorderStroke(0.5.dp, DividerDark)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                UserAvatar(name = user.name, color = user.avatarColor, size = 52.dp)
                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(if (user.isPlaying) AccentGreen else Color(0xFF4CAF50))
                            .border(2.dp, CardDark, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (user.isPlaying) {
                        PulsingDot(color = AccentGreen, size = 6f)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.playing_now), fontSize = 12.sp, color = AccentGreen, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            if (user.isOnline) stringResource(R.string.online) else stringResource(R.string.offline),
                            fontSize = 12.sp,
                            color = if (user.isOnline) TextSecondary else TextTertiary
                        )
                    }
                }
            }
            
            Button(
                onClick = onInvite,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary.copy(alpha = 0.15f)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Send, null, tint = Primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.invite_button).uppercase(), color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Black)
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
        Icon(Icons.Outlined.SportsEsports, null, modifier = Modifier.size(80.dp), tint = TextTertiary.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(24.dp))
        Text("STILL WAITING...", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Share your ID to start building your squad", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = TextTertiary)
    }
}
