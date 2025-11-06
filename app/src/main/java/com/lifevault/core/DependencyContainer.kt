package com.lifevault.core

import android.content.Context
import androidx.room.Room
import com.lifevault.data.database.LifeVaultDatabase
import com.lifevault.data.repository.AuthRepository
import com.lifevault.data.repository.LifeRepository
import com.lifevault.data.repository.LifeVaultRepository
import com.lifevault.data.network.AuthApi
import com.lifevault.data.network.ProfileApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Service Locator для управления зависимостями приложения.
 *
 * Преимущества:
 * - Простота - никакой магии, всё явно
 * - Гибкость - легко создавать разные конфигурации для тестов
 * - Контроль - явное управление синглтонами
 */
open class DependencyContainer(
    private val context: Context,
    private val baseUrl: String = "http://10.0.2.2:8080"
) {
    // ========== Ленивая инициализация (Singleton) ==========

    open val database: LifeVaultDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            LifeVaultDatabase::class.java,
            "im_not_die_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val profileApi: ProfileApi by lazy {
        retrofit.create(ProfileApi::class.java)
    }

    // ========== DAO ==========

    val userDao by lazy { database.userDao() }
    val userProfileDao by lazy { database.userProfileDao() }
    val habitDao by lazy { database.habitDao() }
    val quitRecordDao by lazy { database.quitRecordDao() }
    val habitEntryDao by lazy { database.habitEntryDao() }

    // ========== Repositories ==========

    val authRepository: AuthRepository by lazy {
        AuthRepository(
            authApi = authApi,
            userDao = userDao,
            userProfileDao = userProfileDao,
            habitDao = habitDao
        )
    }

    val lifeRepository: LifeRepository by lazy {
        LifeRepository(
            userProfileDao = userProfileDao,
            habitDao = habitDao
        )
    }

    val quitSmokingRepository: LifeVaultRepository by lazy {
        LifeVaultRepository(
            quitRecordDao = quitRecordDao
        )
    }
}

/**
 * Глобальный экземпляр контейнера.
 * Инициализируется в Application.onCreate()
 */
lateinit var appContainer: DependencyContainer
