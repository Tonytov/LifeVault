package com.lifevault.data.network

import com.lifevault.data.network.models.TestCleanupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit API интерфейс для тестовых эндпоинтов очистки данных.
 *
 * ВАЖНО: Эти эндпоинты используются ТОЛЬКО для E2E тестов!
 * В production они должны быть отключены на backend.
 */
interface TestCleanupApi {

    /**
     * Удаляет пользователя по номеру телефона
     */
    @POST("/api/test/deleteUser")
    suspend fun deleteUser(@Body request: DeleteUserRequest): Response<TestCleanupResponse>

    /**
     * Удаляет всех пользователей с указанным префиксом номера телефона
     */
    @POST("/api/test/deleteByPrefix")
    suspend fun deleteUsersByPrefix(@Body request: DeleteByPrefixRequest): Response<TestCleanupResponse>

    /**
     * Получает список всех пользователей (для отладки)
     */
    @GET("/api/test/users")
    suspend fun getAllUsers(): Response<Map<String, Any>>

    /**
     * Health check для тестовых эндпоинтов
     */
    @GET("/api/test/health")
    suspend fun healthCheck(): Response<Map<String, Any>>
}

/**
 * Request для удаления пользователя по номеру телефона
 */
data class DeleteUserRequest(
    val phoneNumber: String
)

/**
 * Request для удаления пользователей по префиксу
 */
data class DeleteByPrefixRequest(
    val prefix: String
)