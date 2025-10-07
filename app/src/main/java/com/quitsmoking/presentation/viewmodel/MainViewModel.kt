package com.quitsmoking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quitsmoking.data.model.*
import com.quitsmoking.data.repository.LifeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val habits: List<Habit> = emptyList(),
    val lifeCountdown: LifeCalculator.LifeCountdown = LifeCalculator.LifeCountdown(0, 0, 0, 0, 0, 0),
    val lifeExtensionDays: Int = 0,
    val healthProgress: Float = 0.5f, // 0.0 to 1.0
    val deathDate: LocalDateTime? = null,
    val savingsData: SavingsData = SavingsData()
)

data class SavingsData(
    val totalMoneySaved: Double = 0.0,
    val smokingStreak: Int = 0,
    val alcoholStreak: Int = 0,
    val sugarStreak: Int = 0,
    val smokingSavings: Double = 0.0,
    val alcoholSavings: Double = 0.0
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val lifeRepository: LifeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadUserData()
        startLifeCountdownTimer()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            lifeRepository.getUserProfileWithHabits().collect { (profile, habits) ->
                if (profile != null) {
                    val deathDate = LifeCalculator.calculateRemainingLifeWithHabits(profile, habits)
                    val countdown = LifeCalculator.calculateLifeCountdown(deathDate)
                    val lifeExtension = calculateLifeExtension(habits)
                    val healthProgress = calculateHealthProgress(habits)
                    val savingsData = calculateSavings(habits)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = profile,
                        habits = habits,
                        lifeCountdown = countdown,
                        lifeExtensionDays = lifeExtension,
                        healthProgress = healthProgress,
                        deathDate = deathDate,
                        savingsData = savingsData
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }
    
    private fun startLifeCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // Update every second for real-time countdown
                _uiState.value.deathDate?.let { deathDate ->
                    val countdown = LifeCalculator.calculateLifeCountdown(deathDate)
                    _uiState.value = _uiState.value.copy(lifeCountdown = countdown)
                }
            }
        }
    }
    
    private fun calculateLifeExtension(habits: List<Habit>): Int {
        return habits
            .filter { !it.isActive } // Only quit habits
            .sumOf { it.type.lifeYearsGained } * 365 // Convert years to days
    }
    
    private fun calculateHealthProgress(habits: List<Habit>): Float {
        val totalHabits = HabitType.values().size.toFloat()
        val quitHabits = habits.count { !it.isActive }.toFloat()
        return (0.3f + (quitHabits / totalHabits) * 0.7f).coerceIn(0f, 1f)
    }
    
    fun quitHabit(habitType: HabitType) {
        viewModelScope.launch {
            val existingHabit = _uiState.value.habits.find { it.type == habitType }
            if (existingHabit != null) {
                val updatedHabit = existingHabit.copy(
                    isActive = false,
                    quitDate = LocalDateTime.now()
                )
                lifeRepository.updateHabit(updatedHabit)
            } else {
                // Create new habit as already quit
                val newHabit = Habit(
                    type = habitType,
                    quitDate = LocalDateTime.now(),
                    isActive = false
                )
                lifeRepository.insertHabit(newHabit)
            }
        }
    }
    
    fun resumeHabit(habitType: HabitType) {
        viewModelScope.launch {
            val existingHabit = _uiState.value.habits.find { it.type == habitType }
            if (existingHabit != null) {
                val updatedHabit = existingHabit.copy(isActive = true)
                lifeRepository.updateHabit(updatedHabit)
            }
        }
    }
    
    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            lifeRepository.insertHabit(habit)
        }
    }
    
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            lifeRepository.updateHabit(habit)
        }
    }
    
    private fun calculateSavings(habits: List<Habit>): SavingsData {
        var totalSavings = 0.0
        var smokingStreak = 0
        var alcoholStreak = 0
        var sugarStreak = 0
        var smokingSavings = 0.0
        var alcoholSavings = 0.0
        
        
        // Временно для тестирования - показываем данные даже для активных привычек
        habits.forEach { habit ->
            // Если привычка неактивна и есть дата отказа
            if (!habit.isActive && habit.quitDate != null) {
                val daysSinceQuit = java.time.temporal.ChronoUnit.DAYS.between(
                    habit.quitDate!!.toLocalDate(),
                    java.time.LocalDate.now()
                ).toInt()
                
                when (habit.type) {
                    HabitType.SMOKING -> {
                        smokingStreak = daysSinceQuit
                        if (habit.costPerUnit > 0 && habit.dailyUsage != null) {
                            smokingSavings = daysSinceQuit * (habit.costPerUnit * habit.dailyUsage!!)
                            totalSavings += smokingSavings
                        }
                    }
                    HabitType.ALCOHOL -> {
                        alcoholStreak = daysSinceQuit
                        if (habit.costPerUnit > 0) {
                            // For alcohol, costPerUnit is cost per session, and we need to calculate daily savings
                            val weeklySessions = (habit.weeklyUsage ?: 0) / 100 // Rough estimate
                            val dailySessions = weeklySessions / 7.0
                            alcoholSavings = daysSinceQuit * (habit.costPerUnit * dailySessions)
                            totalSavings += alcoholSavings
                        }
                    }
                    HabitType.SUGAR -> {
                        sugarStreak = daysSinceQuit
                        // Sugar savings calculation could be added if cost tracking is implemented
                    }
                }
            }
            // Временно для тестирования - показываем активные привычки как тестовые данные
            else if (habit.isActive) {
                when (habit.type) {
                    HabitType.SMOKING -> {
                        smokingStreak = 5 // Тестовое значение
                        smokingSavings = 500.0 // Тестовое значение
                        totalSavings += smokingSavings
                    }
                    HabitType.ALCOHOL -> {
                        alcoholStreak = 10 // Тестовое значение
                        alcoholSavings = 800.0 // Тестовое значение
                        totalSavings += alcoholSavings
                    }
                    HabitType.SUGAR -> {
                        sugarStreak = 3 // Тестовое значение
                    }
                }
            }
        }
        
        return SavingsData(
            totalMoneySaved = totalSavings,
            smokingStreak = smokingStreak,
            alcoholStreak = alcoholStreak,
            sugarStreak = sugarStreak,
            smokingSavings = smokingSavings,
            alcoholSavings = alcoholSavings
        )
    }
}