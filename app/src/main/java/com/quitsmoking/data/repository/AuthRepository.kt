package com.quitsmoking.data.repository

import com.quitsmoking.data.dao.UserDao
import com.quitsmoking.data.dao.UserProfileDao
import com.quitsmoking.data.dao.HabitDao
import com.quitsmoking.data.model.*
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val userProfileDao: UserProfileDao,
    private val habitDao: HabitDao
) {
    
    fun getCurrentUser(): Flow<User?> = userDao.getCurrentUserFlow()
    
    suspend fun isUserLoggedIn(): Boolean {
        return userDao.getCurrentUser() != null
    }
    
    suspend fun login(phoneNumber: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByPhone(phoneNumber)
            if (user == null) {
                Result.failure(Exception("Пользователь не найден"))
            } else if (!user.isPhoneVerified) {
                Result.failure(Exception("Номер телефона не подтвержден"))
            } else if (user.passwordHash != hashPassword(password)) {
                Result.failure(Exception("Неверный пароль"))
            } else {
                userDao.updateLastLogin(phoneNumber, LocalDateTime.now())
                Result.success(user.copy(lastLoginAt = LocalDateTime.now()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(phoneNumber: String, password: String): Result<User> {
        return try {
            val formattedPhone = PhoneValidator.formatPhoneNumber(phoneNumber)
            
            if (!PhoneValidator.validatePhoneNumber(formattedPhone)) {
                return Result.failure(Exception("Неверный формат номера телефона"))
            }
            
            val existingUser = userDao.getUserByPhone(formattedPhone)
            if (existingUser != null) {
                return Result.failure(Exception("Пользователь с таким номером уже существует"))
            }
            
            val passwordValidation = PasswordValidator.validate(password)
            if (!passwordValidation.isValid) {
                return Result.failure(Exception(passwordValidation.errors.joinToString("\n")))
            }
            
            val user = User(
                phoneNumber = formattedPhone,
                passwordHash = hashPassword(password),
                isPhoneVerified = false
            )
            
            // Очищаем данные предыдущих пользователей
            userDao.deleteAllUsers()
            userProfileDao.deleteUserProfile()
            habitDao.deleteAllHabits()
            
            userDao.insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendVerificationCode(phoneNumber: String): Result<String> {
        return try {
            val formattedPhone = PhoneValidator.formatPhoneNumber(phoneNumber)
            val user = userDao.getUserByPhone(formattedPhone)
            
            if (user == null) {
                return Result.failure(Exception("Пользователь не найден"))
            }
            
            val code = generateVerificationCode()
            val expiry = LocalDateTime.now().plusMinutes(5)
            
            userDao.setVerificationCode(formattedPhone, code, expiry)
            
            // В реальном приложении здесь был бы вызов SMS API
            // Для демонстрации просто возвращаем код
            Result.success(code)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyCode(phoneNumber: String, code: String): Result<Boolean> {
        return try {
            val formattedPhone = PhoneValidator.formatPhoneNumber(phoneNumber)
            val user = userDao.getUserByPhone(formattedPhone)
            
            if (user == null) {
                return Result.failure(Exception("Пользователь не найден"))
            }
            
            if (user.verificationCode != code) {
                return Result.failure(Exception("Неверный код подтверждения"))
            }
            
            if (user.verificationCodeExpiry?.isBefore(LocalDateTime.now()) == true) {
                return Result.failure(Exception("Код подтверждения истек"))
            }
            
            userDao.updatePhoneVerification(formattedPhone, true)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        userDao.deleteAllUsers()
        userProfileDao.deleteUserProfile()
        habitDao.deleteAllHabits()
    }
    
    // Метод для тестирования - удаляет всех пользователей
    suspend fun clearAllUsers() {
        userDao.deleteAllUsers()
        userProfileDao.deleteUserProfile()
        habitDao.deleteAllHabits()
    }
    
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
    
    private fun generateVerificationCode(): String {
        // Для демо-версии всегда возвращаем 111111
        return "111111"
        // В продакшене использовать:
        // return Random.nextInt(100000, 999999).toString()
    }
}