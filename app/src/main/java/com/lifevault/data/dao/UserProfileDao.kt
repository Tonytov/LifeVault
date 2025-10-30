package com.lifevault.data.dao

import androidx.room.*
import com.lifevault.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)
    
    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)
    
    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}