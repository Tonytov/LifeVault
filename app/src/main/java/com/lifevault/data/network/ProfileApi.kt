package com.lifevault.data.network

import com.lifevault.data.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API интерфейс для работы с Profile endpoints на Backend (Ktor).
 *
 * Base URL: http://10.0.2.2:8080 (для эмулятора)
 *           http://localhost:8080 (для реального устройства через WiFi)
 */
interface ProfileApi {

    /**
     * Создание профиля пользователя
     *
     * @param request CreateProfileRequest с данными профиля
     * @return ProfileResponse с созданным профилем
     */
    @POST("/api/profile")
    suspend fun createProfile(@Body request: CreateProfileRequest): Response<ProfileResponse>

    /**
     * Получение профиля пользователя по userId
     *
     * @param userId ID пользователя
     * @return ProfileResponse с данными профиля
     */
    @GET("/api/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: Int): Response<ProfileResponse>

    /**
     * Обновление профиля пользователя
     *
     * @param userId ID пользователя
     * @param request CreateProfileRequest с обновленными данными
     * @return ProfileResponse с обновленным профилем
     */
    @PUT("/api/profile/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: Int,
        @Body request: CreateProfileRequest
    ): Response<ProfileResponse>
}