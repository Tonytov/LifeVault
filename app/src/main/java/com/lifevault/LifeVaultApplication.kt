package com.lifevault

import android.app.Application
import com.lifevault.core.DependencyContainer
import com.lifevault.core.appContainer

class LifeVaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализируем Service Locator
        appContainer = DependencyContainer(
            context = applicationContext,
            baseUrl = "http://10.0.2.2:8080"
        )
    }
}