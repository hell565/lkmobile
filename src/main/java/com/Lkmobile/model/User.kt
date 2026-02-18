package com.Lkmobile.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("accessId")
    val accessId: String = "",

    @SerializedName("isPlaying")
    val isPlaying: Boolean = false,

    @SerializedName("isOnline")
    val isOnline: Boolean = false,

    @SerializedName("lastSeen")
    val lastSeen: Long = System.currentTimeMillis(),

    @SerializedName("avatarColor")
    val avatarColor: Int = 0
)

data class AuthRequest(
    @SerializedName("accessId")
    val accessId: String
)

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("userId")
    val userId: String = ""
)

data class RegisterRequest(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("accessId")
    val accessId: String
)

data class RegisterResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("user")
    val user: User? = null,

    @SerializedName("message")
    val message: String = ""
)

data class UsersResponse(
    @SerializedName("users")
    val users: List<User> = emptyList()
)

data class StatusUpdateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("isPlaying")
    val isPlaying: Boolean,

    @SerializedName("isOnline")
    val isOnline: Boolean,

    @SerializedName("userId")
    val userId: String = ""
)

data class InviteRequest(
    @SerializedName("fromUserId")
    val fromUserId: String,

    @SerializedName("toUserId")
    val toUserId: String,

    @SerializedName("fromUserName")
    val fromUserName: String
)

data class InviteResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String = ""
)

data class InvitesResponse(
    @SerializedName("invites")
    val invites: List<InviteData> = emptyList()
)

data class InviteData(
    @SerializedName("id")
    val id: String,
    @SerializedName("fromName")
    val fromName: String,
    @SerializedName("time")
    val time: Long
)

data class ChatResponse(
    @SerializedName("messages")
    val messages: List<ChatMessage> = emptyList()
)

data class ChatMessage(
    @SerializedName("id")
    val id: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("time")
    val time: Long,
    @SerializedName("color")
    val color: Int
)

data class ChatMessageRequest(
    @SerializedName("from")
    val from: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("color")
    val color: Int
)
