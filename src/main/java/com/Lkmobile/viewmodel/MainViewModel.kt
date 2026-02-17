package com.Lkmobile.viewmodel

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Lkmobile.data.ApiClient
import com.Lkmobile.data.UserPreferences
import com.Lkmobile.model.*
import com.Lkmobile.util.GameDetector
import com.Lkmobile.util.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AppState(
    val isLoggedIn: Boolean = false,
    val hasName: Boolean = false,
    val userId: String = "",
    val userName: String = "",
    val accessId: String = "",
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGameRunning: Boolean = false,
    val loginSuccess: Boolean? = null,
    val registerSuccess: Boolean? = null,
    val inviteSent: Boolean = false,
    val useMockMode: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private var gameCheckRunnable: Runnable? = null

    private val mockUsers = mutableListOf<User>()
    private val avatarColors = listOf(
        0xFF6C63FF.toInt(), 0xFF00D9FF.toInt(), 0xFFFF5252.toInt(),
        0xFF00E676.toInt(), 0xFFFFAB40.toInt(), 0xFF7C4DFF.toInt(),
        0xFF448AFF.toInt(), 0xFFE040FB.toInt(), 0xFF18FFFF.toInt()
    )

    init {
        _state.value = _state.value.copy(useMockMode = false)
        loadSavedState()
    }

    private fun loadSavedState() {
        viewModelScope.launch {
            combine(
                prefs.isLoggedIn,
                prefs.hasName,
                prefs.userId,
                prefs.userName,
                prefs.accessId
            ) { isLoggedIn, hasName, userId, userName, accessId ->
                _state.value.copy(
                    isLoggedIn = true,
                    hasName = hasName,
                    userId = userId.ifEmpty { "user_${System.currentTimeMillis()}" },
                    userName = userName,
                    accessId = "default_group"
                )
            }.collect { newState ->
                _state.value = newState
                if (newState.isLoggedIn && newState.hasName) {
                    startGameCheck()
                    loadUsers()
                }
            }
        }
    }

    fun verifyId(accessId: String) {
        if (accessId.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter your ID")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            if (_state.value.useMockMode) {
                delay(800)
                val userId = "user_${System.currentTimeMillis()}"
                prefs.saveLoginData(accessId, userId)
                _state.value = _state.value.copy(
                    isLoading = false,
                    loginSuccess = true,
                    isLoggedIn = true,
                    userId = userId,
                    accessId = accessId
                )
                return@launch
            }

            try {
                val response = ApiClient.apiService.verifyId(AuthRequest(accessId))
                if (response.isSuccessful && response.body()?.success == true) {
                    val userId = response.body()!!.userId
                    prefs.saveLoginData(accessId, userId)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginSuccess = true,
                        isLoggedIn = true,
                        userId = userId,
                        accessId = accessId
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = response.body()?.message ?: "Invalid ID",
                        loginSuccess = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Connection error: ${e.localizedMessage}",
                    loginSuccess = false
                )
            }
        }
    }

    fun registerName(name: String) {
        if (name.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter your name")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val request = RegisterRequest(
                    userId = _state.value.userId,
                    name = name,
                    accessId = "default_group"
                )
                val response = ApiClient.apiService.registerUser(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    prefs.saveUserName(name)
                    if (user != null) {
                        prefs.saveLoginData("default_group", user.id)
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        registerSuccess = true,
                        hasName = true,
                        userName = name,
                        userId = user?.id ?: _state.value.userId
                    )
                    loadUsers()
                    startGameCheck()
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = response.body()?.message ?: "Registration failed",
                        registerSuccess = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Connection error: ${e.localizedMessage}",
                    registerSuccess = false
                )
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getUsers("default_group")
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(
                        users = response.body()?.users ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                // Silently fail, will retry on next poll
            }
        }
    }

    fun sendInvite(toUser: User) {
        viewModelScope.launch {
            if (_state.value.useMockMode) {
                val context = getApplication<Application>()
                NotificationHelper.showInviteNotification(
                    context,
                    _state.value.userName,
                    toUser.hashCode()
                )
                _state.value = _state.value.copy(inviteSent = true)
                delay(2000)
                _state.value = _state.value.copy(inviteSent = false)
                return@launch
            }

            try {
                val request = InviteRequest(
                    fromUserId = _state.value.userId,
                    toUserId = toUser.id,
                    fromUserName = _state.value.userName
                )
                val response = ApiClient.apiService.sendInvite(request)
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(inviteSent = true)
                    delay(2000)
                    _state.value = _state.value.copy(inviteSent = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to send invite"
                )
            }
        }
    }

    fun launchGame() {
        val context = getApplication<Application>()
        GameDetector.launchGame(context)
    }

    private fun startGameCheck() {
        stopGameCheck()
        gameCheckRunnable = object : Runnable {
            override fun run() {
                checkGameStatus()
                handler.postDelayed(this, 3000)
            }
        }
        handler.post(gameCheckRunnable!!)
    }

    private fun stopGameCheck() {
        gameCheckRunnable?.let { handler.removeCallbacks(it) }
        gameCheckRunnable = null
    }

    private fun checkGameStatus() {
        val context = getApplication<Application>()
        val isPlaying = GameDetector.isGameRunning(context)
        if (_state.value.isGameRunning != isPlaying) {
            _state.value = _state.value.copy(isGameRunning = isPlaying)
            updateStatusOnServer(isPlaying)
        }
    }

    private fun updateStatusOnServer(isPlaying: Boolean) {
        viewModelScope.launch {
            if (_state.value.useMockMode) {
                val updatedUsers = mockUsers.map {
                    if (it.id == _state.value.userId) it.copy(isPlaying = isPlaying) else it
                }
                mockUsers.clear()
                mockUsers.addAll(updatedUsers)
                _state.value = _state.value.copy(users = mockUsers.toList())
                return@launch
            }

            try {
                ApiClient.apiService.updateStatus(
                    StatusUpdateRequest(
                        name = _state.value.userName,
                        isPlaying = isPlaying,
                        isOnline = true,
                        userId = _state.value.userId
                    )
                )
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun logout() {
        viewModelScope.launch {
            stopGameCheck()
            prefs.clearAll()
            mockUsers.clear()
            _state.value = AppState()
        }
    }

    fun setMockMode(enabled: Boolean) {
        _state.value = _state.value.copy(useMockMode = enabled)
    }

    override fun onCleared() {
        super.onCleared()
        stopGameCheck()
    }
}
