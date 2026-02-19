package com.Lkmobile.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Lkmobile.ui.screens.*
import com.Lkmobile.ui.theme.*
import com.Lkmobile.viewmodel.MainViewModel

object Routes {
    const val LOGIN = "login"
    const val NAME_ENTRY = "name_entry"
    const val USER_LIST = "user_list"
    const val CHAT = "chat"
    const val SETTINGS = "settings"
    const val LOBBY = "lobby"
    const val LOGS = "logs"
    const val LOBBY_CHAT = "lobby_chat/{lobbyId}"
}

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf(Routes.USER_LIST) }

    Scaffold(
        bottomBar = {
            if (state.hasName && currentRoute in listOf(Routes.USER_LIST, Routes.CHAT, Routes.LOBBY, Routes.SETTINGS)) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.USER_LIST,
                        onClick = { 
                            currentRoute = Routes.USER_LIST
                            navController.navigate(Routes.USER_LIST) {
                                popUpTo(Routes.USER_LIST) { inclusive = true }
                            }
                        },
                        icon = { Icon(if (currentRoute == Routes.USER_LIST) Icons.Filled.Home else Icons.Outlined.Home, null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.LOBBY,
                        onClick = { 
                            currentRoute = Routes.LOBBY
                            navController.navigate(Routes.LOBBY) 
                        },
                        icon = { Icon(if (currentRoute == Routes.LOBBY) Icons.Filled.Groups else Icons.Outlined.Groups, null) },
                        label = { Text("Lobby") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.CHAT,
                        onClick = { 
                            currentRoute = Routes.CHAT
                            navController.navigate(Routes.CHAT) 
                        },
                        icon = { Icon(if (currentRoute == Routes.CHAT) Icons.Filled.Chat else Icons.Outlined.Chat, null) },
                        label = { Text("Chat") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.SETTINGS,
                        onClick = { 
                            currentRoute = Routes.SETTINGS
                            navController.navigate(Routes.SETTINGS) 
                        },
                        icon = { Icon(if (currentRoute == Routes.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings, null) },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.NAME_ENTRY,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    state = state,
                    onVerifyId = { viewModel.verifyId(it) },
                    onClearError = { viewModel.clearError() }
                )
            }

            composable(Routes.NAME_ENTRY) {
                NameEntryScreen(
                    state = state,
                    onRegisterName = { viewModel.registerName(it) },
                    onClearError = { viewModel.clearError() }
                )
            }

            composable(Routes.USER_LIST) {
                UserListScreen(
                    state = state,
                    onInvite = { viewModel.sendInvite(it) },
                    onLaunchGame = { viewModel.launchGame() },
                    onLogout = { viewModel.logout() },
                    onRefresh = { viewModel.loadUsers() },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenChat = { navController.navigate(Routes.CHAT) },
                    onOpenLobby = { navController.navigate(Routes.LOBBY) }
                )
            }

            composable(Routes.CHAT) {
                ChatScreen(
                    state = state,
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.LOBBY) {
                LobbyScreen(
                    state = state,
                    onCreateLobby = { viewModel.createLobby(it) },
                    onJoinLobby = { lobbyId -> 
                        viewModel.joinLobby(lobbyId)
                        navController.navigate("lobby_chat/$lobbyId")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.LOBBY_CHAT) { backStackEntry ->
                val lobbyId = backStackEntry.arguments?.getString("lobbyId")
                val lobby = state.lobbies.find { it.id == lobbyId }
                if (lobby != null) {
                    LobbyChatScreen(
                        state = state,
                        lobby = lobby,
                        onSendMessage = { viewModel.sendLobbyChatMessage(lobbyId!!, it) },
                        onLeave = { 
                            viewModel.leaveLobby(lobbyId!!)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.LOGS) {
                LogScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    state = state,
                    onToggleMock = { viewModel.setMockMode(it) },
                    onBack = { navController.popBackStack() },
                    onOpenLogs = { navController.navigate(Routes.LOGS) }
                )
            }
        }
    }
}
