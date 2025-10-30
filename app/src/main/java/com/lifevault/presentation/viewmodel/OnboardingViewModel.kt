package com.lifevault.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifevault.data.dao.UserDao
import com.lifevault.data.dao.UserProfileDao
import com.lifevault.data.model.Gender
import com.lifevault.data.model.LifeCalculator
import com.lifevault.data.model.LifeExpectancyRegion
import com.lifevault.data.model.UserProfile
import com.lifevault.data.network.ProfileApi
import com.lifevault.data.network.models.CreateProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class OnboardingViewModel(
    private val userProfileDao: UserProfileDao,
    private val userDao: UserDao,
    private val profileApi: ProfileApi
) : ViewModel() {

    companion object {
        private const val TAG = "OnboardingViewModel"
    }
    
    private val _isProfileCreated = MutableStateFlow(false)
    val isProfileCreated: StateFlow<Boolean> = _isProfileCreated.asStateFlow()
    
    fun createUserProfile(
        fullName: String,
        gender: Gender,
        birthDate: LocalDate,
        height: Int,
        weight: Int,
        region: LifeExpectancyRegion
    ) {
        viewModelScope.launch {
            try {
                // Получаем текущего пользователя
                val currentUser = userDao.getLoggedInUser()
                if (currentUser == null) {
                    Log.e(TAG, "Нет залогиненного пользователя")
                    return@launch
                }

                val profile = UserProfile(
                    fullName = fullName,
                    gender = gender,
                    birthDate = birthDate,
                    height = height,
                    weight = weight,
                    region = region,
                    baseLifeExpectancy = LifeCalculator.calculateBaseLifeExpectancy(
                        UserProfile(
                            fullName = fullName,
                            gender = gender,
                            birthDate = birthDate,
                            height = height,
                            weight = weight,
                            region = region,
                            baseLifeExpectancy = 0 // Will be calculated
                        )
                    )
                )

                // Сохраняем локально
                userProfileDao.insertUserProfile(profile)

                // Отправляем на бэкенд
                Log.d(TAG, "Отправка профиля на бэкенд для userId: ${currentUser.id}")
                val request = CreateProfileRequest(
                    userId = currentUser.id.toInt(),
                    fullName = fullName,
                    gender = gender.name,
                    birthDate = birthDate.toString(), // ISO format: YYYY-MM-DD
                    height = height,
                    weight = weight,
                    region = region.name
                )

                val response = profileApi.createProfile(request)
                if (response.isSuccessful) {
                    Log.d(TAG, "Профиль успешно создан на бэкенде: ${response.body()}")
                    _isProfileCreated.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Ошибка создания профиля на бэкенде: $errorBody")
                    // Даже если бэкенд недоступен, профиль сохранен локально
                    _isProfileCreated.value = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при создании профиля", e)
                // Даже при ошибке профиль сохранен локально
                _isProfileCreated.value = true
            }
        }
    }
}