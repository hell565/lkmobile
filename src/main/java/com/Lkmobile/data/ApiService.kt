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
}
