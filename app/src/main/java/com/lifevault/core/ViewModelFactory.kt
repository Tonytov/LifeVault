package com.lifevault.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifevault.presentation.ui.splash.SplashViewModel
import com.lifevault.presentation.viewmodel.AuthViewModel
import com.lifevault.presentation.viewmodel.MainViewModel
import com.lifevault.presentation.viewmodel.OnboardingViewModel
import com.lifevault.presentation.viewmodel.StatisticsViewModel
import com.lifevault.presentation.viewmodel.TodayViewModel

/**
 * ViewModelFactory для создания ViewModels с зависимостями из appContainer.
 *
 * Использование:
 * val viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(appContainer))
 */
class ViewModelFactory(
    private val container: DependencyContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(
                authRepository = container.authRepository,
                lifeRepository = container.lifeRepository
            ) as T

            MainViewModel::class.java -> MainViewModel(
                lifeRepository = container.lifeRepository
            ) as T

            OnboardingViewModel::class.java -> OnboardingViewModel(
                userProfileDao = container.database.userProfileDao(),
                userDao = container.database.userDao(),
                profileApi = container.profileApi
            ) as T

            StatisticsViewModel::class.java -> StatisticsViewModel(
                lifeRepository = container.lifeRepository
            ) as T

            SplashViewModel::class.java -> SplashViewModel(
                authRepository = container.authRepository,
                lifeRepository = container.lifeRepository
            ) as T

            TodayViewModel::class.java -> TodayViewModel(
                habitEntryDao = container.habitEntryDao,
                lifeRepository = container.lifeRepository
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}