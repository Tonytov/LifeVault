package com.lifevault.data.repository

import com.lifevault.data.dao.UserProfileDao
import com.lifevault.data.dao.HabitDao
import com.lifevault.data.model.UserProfile
import com.lifevault.data.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class LifeRepository(
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