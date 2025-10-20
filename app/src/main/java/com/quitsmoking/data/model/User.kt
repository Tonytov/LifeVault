package com.quitsmoking.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Long = 1,
    val phoneNumber: String,
    val passwordHash: String,
    val fullName: String? = null,
    val isPhoneVerified: Boolean = false,
    val verificationCode: String? = null,
    val verificationCodeExpiry: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime? = null,
    val isLoggedIn: Boolean = false
)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val pendingPhoneNumber: String? = null,
    val isVerificationSent: Boolean = false
)

data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

data class RegisterRequest(
    val phoneNumber: String,
    val password: String
)

data class VerificationRequest(
    val phoneNumber: String,
    val code: String
)

object PasswordValidator {
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )
    
    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (password.length < 6) {
            errors.add("Пароль должен содержать минимум 6 символов")
        }
        
        if (!password.any { it.isDigit() }) {
            errors.add("Пароль должен содержать минимум 1 цифру")
        }
        
        if (!password.any { it.isLetter() }) {
            errors.add("Пароль должен содержать минимум 1 букву")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}

object PhoneValidator {
    fun validatePhoneNumber(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[\\s\\-\\(\\)\\+]"), "")
        return cleanPhone.matches(Regex("^[78]\\d{10}$")) // Russian phone format
    }

    /**
     * Проверяет, является ли номер телефона валидным (для тестов и UI)
     * @param phone номер телефона (может быть с форматированием)
     * @return true если номер валиден
     */
    fun isValidPhone(phone: String): Boolean {
        val digitsOnly = phone.filter { it.isDigit() }
        return digitsOnly.length == 11 && (digitsOnly.startsWith("7") || digitsOnly.startsWith("8"))
    }

    fun formatPhoneNumber(phone: String): String {
        val cleanPhone = phone.replace(Regex("[^\\d]"), "")
        return when {
            cleanPhone.startsWith("8") && cleanPhone.length == 11 ->
                "+7" + cleanPhone.substring(1)
            cleanPhone.startsWith("7") && cleanPhone.length == 11 ->
                "+" + cleanPhone
            else -> phone
        }
    }

    data class PhoneInputResult(
        val formattedValue: String,
        val cursorPosition: Int
    )

    fun formatPhoneInput(input: String, currentCursorPosition: Int = input.length): PhoneInputResult {
        val digits = input.replace(Regex("[^\\d]"), "")

        // Ограничиваем ввод номера телефона 11 цифрами (включая код страны)
        val limitedDigits = if (digits.length > 11) digits.take(11) else digits

        return when {
            limitedDigits.isEmpty() -> PhoneInputResult("", 0)
            limitedDigits.startsWith("9") && limitedDigits.length <= 10 -> {
                val formatted = "+7$limitedDigits"
                val newCursorPosition = minOf(formatted.length, currentCursorPosition + 2) // +2 for "+7"
                PhoneInputResult(formatted, newCursorPosition)
            }
            limitedDigits.startsWith("79") -> {
                val formatted = "+7${limitedDigits.substring(1)}"
                PhoneInputResult(formatted, formatted.length)
            }
            limitedDigits.startsWith("89") -> {
                val formatted = "+7${limitedDigits.substring(1)}"
                PhoneInputResult(formatted, formatted.length)
            }
            limitedDigits.startsWith("7") -> {
                val formatted = "+$limitedDigits"
                PhoneInputResult(formatted, formatted.length)
            }
            else -> PhoneInputResult(input.take(15), minOf(currentCursorPosition, 15)) // Лимит для других форматов
        }
    }
}