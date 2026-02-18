package com.Lkmobile.data

import com.Lkmobile.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/verify")
    suspend fun verifyId(@Body request: AuthRequest): Response<AuthResponse>

    @POST("/api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/api/users")
    suspend fun getUsers(@Query("accessId") accessId: String): Response<UsersResponse>

    @POST("/api/users/status")
    suspend fun updateStatus(@Body request: StatusUpdateRequest): Response<Unit>

    @POST("/api/invite")
    suspend fun sendInvite(@Body request: InviteRequest): Response<InviteResponse>

    @GET("/api/invites/{userId}")
    suspend fun getInvites(@Path("userId") userId: String): Response<InvitesResponse>

    @GET("/api/chat")
    suspend fun getChatMessages(): Response<ChatResponse>

    @POST("/api/chat")
    suspend fun sendChatMessage(@Body message: ChatMessageRequest): Response<Unit>

    @GET("/api/chat/private")
    suspend fun getPrivateMessages(@Query("userId") userId: String): Response<ChatResponse>

    @POST("/api/chat/private")
    suspend fun sendPrivateMessage(@Body request: PrivateMessageRequest): Response<Unit>

    @GET("/api/lobbies")
    suspend fun getLobbies(): Response<LobbiesResponse>

    @POST("/api/lobbies")
    suspend fun createLobby(@Body request: CreateLobbyRequest): Response<LobbyActionResponse>

    @POST("/api/lobbies/{lobbyId}/join")
    suspend fun joinLobby(@Path("lobbyId") lobbyId: String, @Body request: JoinLobbyRequest): Response<Unit>

    @GET("/api/lobbies/{lobbyId}/chat")
    suspend fun getLobbyChat(@Path("lobbyId") lobbyId: String): Response<ChatResponse>

    @POST("/api/lobbies/{lobbyId}/chat")
    suspend fun sendLobbyChat(@Path("lobbyId") lobbyId: String, @Body request: ChatMessageRequest): Response<Unit>
}
