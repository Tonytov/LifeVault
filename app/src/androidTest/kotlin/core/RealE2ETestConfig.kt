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


class RealE2ETestConfig(
    private val context: Context
) {
    companion object {
        private const val REAL_BACKEND_URL = "http://10.0.2.2:8080"
    }

    fun createRealContainer(): DependencyContainer {
        return DependencyContainer(
            context = context,
            baseUrl = REAL_BACKEND_URL
        )
    }

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

    fun cleanupUserByPhone(phoneNumber: String): Boolean = runBlocking {
        try {
            val response = testCleanupApi.deleteUser(DeleteUserRequest(phoneNumber))
            if (response.isSuccessful) {
                val body = response.body()
                println("Cleanup: ${body?.message} (deleted: ${body?.deletedCount})")
                return@runBlocking body?.success == true
            } else {
                println("Cleanup failed: ${response.code()} - ${response.message()}")
                return@runBlocking false
            }
        } catch (e: Exception) {
            println("Cleanup error: ${e.message}")
            e.printStackTrace()
            return@runBlocking false
        }
    }

    fun cleanupUsersByPrefix(prefix: String): Int = runBlocking {
        try {
            val response = testCleanupApi.deleteUsersByPrefix(DeleteByPrefixRequest(prefix))
            if (response.isSuccessful) {
                val body = response.body()
                println("Cleanup: ${body?.message}")
                return@runBlocking body?.deletedCount ?: 0
            } else {
                println("Cleanup failed: ${response.code()} - ${response.message()}")
                return@runBlocking 0
            }
        } catch (e: Exception) {
            println("Cleanup error: ${e.message}")
            e.printStackTrace()
            return@runBlocking 0
        }
    }


    fun isBackendAvailable(): Boolean = runBlocking {
        try {
            val response = testCleanupApi.healthCheck()
            val available = response.isSuccessful
            if (available) {
                println("Backend доступен на $REAL_BACKEND_URL")
            } else {
                println("⚠Backend недоступен: ${response.code()}")
            }
            return@runBlocking available
        } catch (e: Exception) {
            println("Backend недоступен: ${e.message}")
            return@runBlocking false
        }
    }

    fun clearLocalDatabase(container: DependencyContainer) {
        runBlocking {
            try {
                // Очищаем все таблицы
                container.database.clearAllTables()
                println("Локальная база данных очищена")
            } catch (e: Exception) {
                println("Ошибка очистки локальной БД: ${e.message}")
            }
        }
    }

    fun fullCleanup(phoneNumber: String, container: DependencyContainer) {
        // 1. Очищаем бэкенд
        cleanupUserByPhone(phoneNumber)

        // 2. Очищаем локальную БД
        clearLocalDatabase(container)
    }
}