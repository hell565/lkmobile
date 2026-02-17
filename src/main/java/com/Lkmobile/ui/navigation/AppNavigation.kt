package com.Lkmobile.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Lkmobile.ui.screens.LoginScreen
import com.Lkmobile.ui.screens.NameEntryScreen
import com.Lkmobile.ui.screens.UserListScreen
import com.Lkmobile.viewmodel.MainViewModel

object Routes {
    const val LOGIN = "login"
    const val NAME_ENTRY = "name_entry"
    const val USER_LIST = "user_list"
}

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    val startDestination = Routes.NAME_ENTRY

    LaunchedEffect(state.isLoggedIn, state.hasName) {
        val target = if (state.hasName) Routes.USER_LIST else Routes.NAME_ENTRY

        val current = navController.currentDestination?.route
        if (current != target) {
            navController.navigate(target) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { it / 3 },
                animationSpec = tween(300)
            )
        }
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
                onRefresh = { viewModel.loadUsers() }
            )
        }
    }
}
