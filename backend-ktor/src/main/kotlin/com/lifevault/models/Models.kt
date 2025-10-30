package com.lifevault.models

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

// ============= REQUEST MODELS =============

@Serializable
data class RegisterRequest(
    val phoneNumber: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

@Serializable
data class VerifyCodeRequest(
    val phoneNumber: String,
    val code: String
)

@Serializable
data class CreateProfileRequest(
    val userId: Int,
    val fullName: String?,
    val gender: String?,
    val birthDate: String?,  // ISO format: "YYYY-MM-DD"
    val height: Int?,
    val weight: Int?,
    val region: String?
)

@Serializable
data class UpdateProfileRequest(
    val fullName: String?,
    val gender: String?,
    val birthDate: String?,  // ISO format: "YYYY-MM-DD"
    val height: Int?,
    val weight: Int?,
    val region: String?
)

// ============= RESPONSE MODELS =============

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserResponse? = null
)

@Serializable
data class UserResponse(
    val id: Int,
    val phoneNumber: String,
    val isPhoneVerified: Boolean,
    val createdAt: String,
    val profile: ProfileResponse? = null
)

@Serializable
data class ProfileResponse(
    val id: Int,
    val fullName: String?,
    val gender: String?,
    val birthDate: String?,
    val height: Int?,
    val weight: Int?,
    val region: String?
)

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val error: String
)

// ============= DATABASE ENTITIES =============

data class User(
    val id: Int,
    val phoneNumber: String,
    val passwordHash: String,
    val isPhoneVerified: Boolean,
    val verificationCode: String?,
    val verificationCodeExpiry: LocalDateTime?,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime?
)

data class UserProfile(
    val id: Int,
    val userId: Int,
    val fullName: String?,
    val gender: String?,
    val birthDate: LocalDate?,
    val height: Int?,
    val weight: Int?,
    val region: String?,
    val createdAt: LocalDateTime
)