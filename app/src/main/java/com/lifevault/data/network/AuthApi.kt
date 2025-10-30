package com.lifevault.data.network

import com.lifevault.data.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit API интерфейс для работы с Backend (Ktor).
 *
 * Base URL: http://10.0.2.2:8080 (для эмулятора)
 *           http://localhost:8080 (для реального устройства через WiFi)
 */
interface AuthApi {

    /**
     * Health check endpoint
     */
    @GET("/health")
    suspend fun healthCheck(): Response<Map<String, Any>>

    /**
     * Регистрация нового пользователя
     *
     * @param request RegisterRequest с phoneNumber и password
     * @return AuthResponse с информацией о созданном пользователе
     */
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    /**
     * Верификация кода подтверждения телефона
     *
     * @param request VerifyCodeRequest с phoneNumber и code
     * @return AuthResponse с обновленным статусом верификации
     */
    @POST("/api/auth/verify")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<AuthResponse>

    /**
     * Вход в систему
     *
     * @param request LoginRequest с phoneNumber и password
     * @return AuthResponse с информацией о пользователе
     */
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
