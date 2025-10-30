package core

import android.content.Context
import com.lifevault.core.DependencyContainer
import com.lifevault.data.network.DeleteByPrefixRequest
import com.lifevault.data.network.DeleteUserRequest
import com.lifevault.data.network.TestCleanupApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Конфигурация для настоящих E2E тестов с реальным backend.
 *
 * Отличия от интеграционных тестов:
 * - ✅ Использует РЕАЛЬНЫЙ бэкенд на http://10.0.2.2:8080
 * - ✅ Использует РЕАЛЬНУЮ базу данных (не in-memory)
 * - ✅ Тестирует РЕАЛЬНУЮ сеть
 * - ✅ Очищает данные на бэкенде через API
 * - ✅ Никаких MockWebServer!
 */
class RealE2ETestConfig(
    private val context: Context
) {
    companion object {
        /**
         * Реальный URL бэкенда.
         * 10.0.2.2 - это алиас для localhost на Android эмуляторе
         */
        private const val REAL_BACKEND_URL = "http://10.0.2.2:8080"
    }

    /**
     * Создаём РЕАЛЬНЫЙ DependencyContainer (НЕ TestDependencyContainer!).
     * Это означает:
     * - Реальная Room база данных (не in-memory)
     * - Реальное подключение к бэкенду
     * - Настоящие сетевые запросы
     */
    fun createRealContainer(): DependencyContainer {
        return DependencyContainer(
            context = context,
            baseUrl = REAL_BACKEND_URL
        )
    }

    /**
     * Создаём TestCleanupApi для очистки тестовых данных на бэкенде.
     */
    private val testCleanupApi: TestCleanupApi by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(REAL_BACKEND_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TestCleanupApi::class.java)
    }

    /**
     * Удаляет пользователя с бэкенда по номеру телефона.
     *
     * @param phoneNumber Номер телефона пользователя
     * @return true если удалён успешно
     */
    fun cleanupUserByPhone(phoneNumber: String): Boolean = runBlocking {
        try {
            val response = testCleanupApi.deleteUser(DeleteUserRequest(phoneNumber))
            if (response.isSuccessful) {
                val body = response.body()
                println("✅ Cleanup: ${body?.message} (deleted: ${body?.deletedCount})")
                return@runBlocking body?.success == true
            } else {
                println("⚠️ Cleanup failed: ${response.code()} - ${response.message()}")
                return@runBlocking false
            }
        } catch (e: Exception) {
            println("❌ Cleanup error: ${e.message}")
            e.printStackTrace()
            return@runBlocking false
        }
    }

    /**
     * Удаляет всех пользователей с указанным префиксом номера телефона.
     * Полезно для массовой очистки после тестов.
     *
     * @param prefix Префикс номера телефона (например, "7543" для тестовых номеров)
     * @return Количество удалённых пользователей
     */
    fun cleanupUsersByPrefix(prefix: String): Int = runBlocking {
        try {
            val response = testCleanupApi.deleteUsersByPrefix(DeleteByPrefixRequest(prefix))
            if (response.isSuccessful) {
                val body = response.body()
                println("✅ Cleanup: ${body?.message}")
                return@runBlocking body?.deletedCount ?: 0
            } else {
                println("⚠️ Cleanup failed: ${response.code()} - ${response.message()}")
                return@runBlocking 0
            }
        } catch (e: Exception) {
            println("❌ Cleanup error: ${e.message}")
            e.printStackTrace()
            return@runBlocking 0
        }
    }

    /**
     * Проверяет доступность бэкенда.
     *
     * @return true если бэкенд доступен
     */
    fun isBackendAvailable(): Boolean = runBlocking {
        try {
            val response = testCleanupApi.healthCheck()
            val available = response.isSuccessful
            if (available) {
                println("✅ Backend доступен на $REAL_BACKEND_URL")
            } else {
                println("⚠️ Backend недоступен: ${response.code()}")
            }
            return@runBlocking available
        } catch (e: Exception) {
            println("❌ Backend недоступен: ${e.message}")
            return@runBlocking false
        }
    }

    /**
     * Очищает локальную базу данных на устройстве.
     *
     * ВАЖНО: Это очищает ТОЛЬКО локальную БД на устройстве,
     * но НЕ очищает бэкенд!
     */
    fun clearLocalDatabase(container: DependencyContainer) {
        runBlocking {
            try {
                // Очищаем все таблицы
                container.database.clearAllTables()
                println("✅ Локальная база данных очищена")
            } catch (e: Exception) {
                println("❌ Ошибка очистки локальной БД: ${e.message}")
            }
        }
    }

    /**
     * Полная очистка: и бэкенд, и локальная БД.
     *
     * @param phoneNumber Номер телефона для очистки с бэкенда
     * @param container DependencyContainer с локальной БД
     */
    fun fullCleanup(phoneNumber: String, container: DependencyContainer) {
        // 1. Очищаем бэкенд
        cleanupUserByPhone(phoneNumber)

        // 2. Очищаем локальную БД
        clearLocalDatabase(container)
    }
}