package com.lifevault.data.network.models

import com.google.gson.annotations.SerializedName

// ============= REQUEST MODELS =============

data class RegisterRequest(
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("password")
    val password: String
)

data class LoginRequest(
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("password")
    val password: String
)

data class VerifyCodeRequest(
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("code")
    val code: String
)

data class CreateProfileRequest(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("birthDate")
    val birthDate: String?,  // ISO format: "YYYY-MM-DD"
    @SerializedName("height")
    val height: Int?,
    @SerializedName("weight")
    val weight: Int?,
    @SerializedName("region")
    val region: String?
)

// ============= RESPONSE MODELS =============

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: UserResponse? = null
)

data class UserResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("isPhoneVerified")
    val isPhoneVerified: Boolean,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("profile")
    val profile: ProfileResponse? = null
)

data class ProfileResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("birthDate")
    val birthDate: String?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("weight")
    val weight: Int?,
    @SerializedName("region")
    val region: String?
)

data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("error")
    val error: String
)

// ============= TEST CLEANUP MODELS =============

/**
 * Response для эндпоинтов очистки тестовых данных.
 * Используется ТОЛЬКО в E2E тестах.
 */
data class TestCleanupResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("deletedCount")
    val deletedCount: Int = 0
)
