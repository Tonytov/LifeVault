package com.quitsmoking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quitsmoking.data.model.*
import com.quitsmoking.data.repository.LifeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val habits: List<Habit> = emptyList(),
    val quitHabits: List<Habit> = emptyList(),
    val totalLifeExtensionDays: Int = 0,
    val healthScore: Float = 0.5f,
    val daysWithoutBadHabits: Int = 0
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val lifeRepository: LifeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    init {
        loadStatisticsData()
    }
    
    private fun loadStatisticsData() {
        viewModelScope.launch {
            lifeRepository.getUserProfileWithHabits().collect { (profile, habits) ->
                val quitHabits = habits.filter { !it.isActive }
                val totalLifeExtension = calculateTotalLifeExtension(quitHabits)
                val healthScore = calculateHealthScore(habits)
                val daysWithoutBadHabits = calculateDaysWithoutBadHabits(quitHabits)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userProfile = profile,
                    habits = habits,
                    quitHabits = quitHabits,
                    totalLifeExtensionDays = totalLifeExtension,
                    healthScore = healthScore,
                    daysWithoutBadHabits = daysWithoutBadHabits
                )
            }
        }
    }
    
    private fun calculateTotalLifeExtension(quitHabits: List<Habit>): Int {
        return quitHabits.sumOf { it.type.lifeYearsGained } * 365
    }
    
    private fun calculateHealthScore(habits: List<Habit>): Float {
        val totalHabits = HabitType.values().size.toFloat()
        val quitHabits = habits.count { !it.isActive }.toFloat()
        return (0.3f + (quitHabits / totalHabits) * 0.7f).coerceIn(0f, 1f)
    }
    
    private fun calculateDaysWithoutBadHabits(quitHabits: List<Habit>): Int {
        if (quitHabits.isEmpty()) return 0
        
        val earliestQuitDate = quitHabits.mapNotNull { it.quitDate }.minOrNull()
        return if (earliestQuitDate != null) {
            ChronoUnit.DAYS.between(earliestQuitDate.toLocalDate(), LocalDateTime.now().toLocalDate()).toInt()
        } else {
            0
        }
    }
}