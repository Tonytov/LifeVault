package com.lifevault

import android.app.Application
import com.lifevault.core.DependencyContainer
import com.lifevault.core.appContainer

/**
 * Test Application class для Android Instrumentation тестов.
 * Инициализирует appContainer для тестового окружения.
 */
class TestLifeVaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализируем Service Locator для тестов
        // Используем тот же baseUrl, что и в основном приложении
        appContainer = DependencyContainer(
            context = applicationContext,
            baseUrl = "http://10.0.2.2:8080"
        )
    }
}
