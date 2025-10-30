package com.lifevault.data.dao

import androidx.room.*
import com.lifevault.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = 1")
    suspend fun getCurrentUser(): User?
    
    @Query("SELECT * FROM users WHERE id = 1")
    fun getCurrentUserFlow(): Flow<User?>
    
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    suspend fun getUserByPhone(phoneNumber: String): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    @Query("UPDATE users SET isPhoneVerified = :isVerified WHERE phoneNumber = :phoneNumber")
    suspend fun updatePhoneVerification(phoneNumber: String, isVerified: Boolean)
    
    @Query("UPDATE users SET verificationCode = :code, verificationCodeExpiry = :expiry WHERE phoneNumber = :phoneNumber")
    suspend fun setVerificationCode(phoneNumber: String, code: String, expiry: java.time.LocalDateTime)
    
    @Query("UPDATE users SET lastLoginAt = :loginTime WHERE phoneNumber = :phoneNumber")
    suspend fun updateLastLogin(phoneNumber: String, loginTime: java.time.LocalDateTime)

    @Query("UPDATE users SET isLoggedIn = :isLoggedIn WHERE phoneNumber = :phoneNumber")
    suspend fun updateLoginStatus(phoneNumber: String, isLoggedIn: Boolean)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): User?

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUserFlow(): Flow<User?>
}