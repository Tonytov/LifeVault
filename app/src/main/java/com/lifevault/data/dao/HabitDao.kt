package com.lifevault.data.dao

import androidx.room.*
import com.lifevault.data.model.Habit
import com.lifevault.data.model.HabitType
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE isActive = 1")
    fun getActiveHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE isActive = 0")
    fun getQuitHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE type = :habitType LIMIT 1")
    fun getHabitByType(habitType: HabitType): Flow<Habit?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)
    
    @Update
    suspend fun updateHabit(habit: Habit)
    
    @Delete
    suspend fun deleteHabit(habit: Habit)
    
    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}