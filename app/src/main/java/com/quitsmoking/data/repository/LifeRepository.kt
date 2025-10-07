package com.quitsmoking.data.repository

import com.quitsmoking.data.dao.UserProfileDao
import com.quitsmoking.data.dao.HabitDao
import com.quitsmoking.data.model.UserProfile
import com.quitsmoking.data.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LifeRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val habitDao: HabitDao
) {
    
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()
    
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()
    
    fun getActiveHabits(): Flow<List<Habit>> = habitDao.getActiveHabits()
    
    fun getQuitHabits(): Flow<List<Habit>> = habitDao.getQuitHabits()
    
    fun getUserProfileWithHabits() = combine(
        getUserProfile(),
        getAllHabits()
    ) { profile, habits ->
        Pair(profile, habits)
    }
    
    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }
    
    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }
    
    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }
    
    suspend fun insertUserProfile(userProfile: UserProfile) {
        userProfileDao.insertUserProfile(userProfile)
    }
}