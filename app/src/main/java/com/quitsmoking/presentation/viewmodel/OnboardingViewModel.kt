package com.quitsmoking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quitsmoking.data.dao.UserProfileDao
import com.quitsmoking.data.model.Gender
import com.quitsmoking.data.model.LifeCalculator
import com.quitsmoking.data.model.LifeExpectancyRegion
import com.quitsmoking.data.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ViewModel() {
    
    private val _isProfileCreated = MutableStateFlow(false)
    val isProfileCreated: StateFlow<Boolean> = _isProfileCreated.asStateFlow()
    
    fun createUserProfile(
        gender: Gender,
        birthDate: LocalDate,
        height: Int,
        weight: Int,
        region: LifeExpectancyRegion
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                gender = gender,
                birthDate = birthDate,
                height = height,
                weight = weight,
                region = region,
                baseLifeExpectancy = LifeCalculator.calculateBaseLifeExpectancy(
                    UserProfile(
                        gender = gender,
                        birthDate = birthDate,
                        height = height,
                        weight = weight,
                        region = region,
                        baseLifeExpectancy = 0 // Will be calculated
                    )
                )
            )
            
            userProfileDao.insertUserProfile(profile)
            _isProfileCreated.value = true
        }
    }
}