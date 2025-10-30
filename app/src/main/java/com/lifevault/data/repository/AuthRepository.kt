package com.lifevault.data.repository

import android.util.Log
import com.lifevault.data.dao.UserDao
import com.lifevault.data.dao.UserProfileDao
import com.lifevault.data.dao.HabitDao
import com.lifevault.data.model.*
import com.lifevault.data.network.AuthApi
import com.lifevault.data.network.models.LoginRequest
import com.lifevault.data.network.models.RegisterRequest
import com.lifevault.data.network.models.VerifyCodeRequest
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

class AuthRepository(
    private val authApi: AuthApi,           // NEW: Backend API
    private val userDao: UserDao,
    private val userProfileDao: UserProfileDao,
    private val habitDao: HabitDao
) {

    companion object {
        private const val TAG = "AuthRepository"
    }
    
    fun getCurrentUser(): Flow<User?> = userDao.getLoggedInUserFlow()

    suspend fun isUserLoggedIn(): Boolean {
        return userDao.getLoggedInUser() != null
    }
    
    suspend fun login(phoneNumber: String, password: String): Result<User> {
        return try {
            val formattedPhone = PhoneValidator.formatPhoneNumber(phoneNumber)

            // Вызов Backend API
            Log.d(TAG, "Логин через Backend API: $formattedPhone")
            val response = authApi.login(LoginRequest(formattedPhone, password))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                if (authResponse.success && authResponse.user != null) {
                    val userResponse = authResponse.user

                    // Обновляем локальную БД
                    userDao.logoutAllUsers()

                    val user = User(
                        id = userResponse.id.toLong(),
                        phoneNumber = userResponse.phoneNumber,
                        passwordHash = hashPassword(password),
                        isPhoneVerified = userResponse.isPhoneVerified,
                        verificationCode = null,
                        verificationCodeExpiry = null,
                        createdAt = parseDateTime(userResponse.createdAt),
                        lastLoginAt = LocalDateTime.now(),
                        isLoggedIn = true
                    )

                    // Сохраняем или обновляем пользователя в локальной БД
                    val existingUser = userDao.getUserByPhone(formattedPhone)
                    if (existingUser != null) {
                        userDao.updateLoginStatus(formattedPhone, true)
                        userDao.updateLastLogin(formattedPhone, LocalDateTime.now())
                    } else {
                        userDao.insertUser(user)
                    }

                    Log.d(TAG, "Логин успешен: ${authResponse.message}")
                    Result.success(user)
                } else {
                    Result.failure(Exception(authResponse.message))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Ошибка входа"
                Log.e(TAG, "Ошибка логина: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при логине", e)
            Result.failure(e)
        }
    }
    
    suspend fun register(phoneNumber: String, password: String): Result<User> {
        return try {
            val formattedPhone = PhoneValidator.formatPhoneNumber(phoneNumber)

            // Валидация на клиенте
            if (!PhoneValidator.validatePhoneNumber(formattedPhone)) {
                return Result.failure(Exception("Неверный формат номера телефона"))
            }

            val passwordValidation = PasswordValidator.validate(password)
            if (!passwordValidation.isValid) {
                return Result.failure(Exception(passwordValidation.errors.joinToString("\n")))
            }

            // Вызов Backend API
            Log.d(TAG, "Регистрация через Backend API: $formattedPhone")
            val response = authApi.register(RegisterRequest(formattedPhone, password))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                if (authResponse.success && authResponse.user != null) {
                    val userResponse = authResponse.user

                    // Сохраняем в локальную БД для offline доступа
                    val user = User(
                        id = userResponse.id.toLong(),
                        phoneNumber = userResponse.phoneNumber,
                        passwordHash = hashPassword(password),
                        isPhoneVerified = userResponse.isPhoneVerified,
                        verificationCode = "111111", // Backend всегда возвращает этот код
                        verificationCodeExpiry = LocalDateTime.now().plusMinutes(5),
                        createdAt = parseDateTime(userResponse.createdAt),
                        lastLoginAt = null,
                        isLoggedIn = false
                    )

                    // Очищаем старые данные и сохраняем нового пользователя
                    userDao.deleteAllUsers()
                    userProfileDao.deleteUserProfile()
                    habitDao.deleteAllHabits()
                    userDao.insertUser(user)

                    Log.d(TAG, "Регистрация успешна: ${authResponse.message}")
                    Result.success(user)
                } else {
                    Result.failure(Exception(authResponse.message))
                }
            } else {
                // Обработка ошибки от сервера
                val errorMessage = response.errorBody()?.string() ?: "Ошибка регистрации"
                Log.e(TAG, "Ошибка регистрации: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при регистрации", e)
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

            // Вызов Backend API
            Log.d(TAG, "Верификация через Backend API: $formattedPhone")
            val response = authApi.verifyCode(VerifyCodeRequest(formattedPhone, code))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                if (authResponse.success) {
                    // Обновляем локальную БД
                    userDao.updatePhoneVerification(formattedPhone, true)
                    // После верификации автоматически логиним пользователя
                    userDao.logoutAllUsers()
                    userDao.updateLoginStatus(formattedPhone, true)

                    Log.d(TAG, "Верификация успешна: ${authResponse.message}")
                    Result.success(true)
                } else {
                    Result.failure(Exception(authResponse.message))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Ошибка верификации"
                Log.e(TAG, "Ошибка верификации: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при верификации", e)
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        // Просто убираем флаг isLoggedIn, не удаляя пользователя
        userDao.logoutAllUsers()
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

    /**
     * Парсит дату-время от Backend API в LocalDateTime
     * Формат: "2025-10-22T23:53:42.635374"
     */
    private fun parseDateTime(dateTimeString: String): LocalDateTime {
        return try {
            LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка парсинга даты: $dateTimeString", e)
            LocalDateTime.now()
        }
    }
}