package com.lifevault.routes

import com.lifevault.database.UserDao
import com.lifevault.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    val userDao = UserDao()

    route("/api/auth") {

        // ============= REGISTER ENDPOINT =============
        post("/register") {
            try {
                println("=== REGISTER REQUEST RECEIVED ===")
                val request = call.receive<RegisterRequest>()
                println("Phone: ${request.phoneNumber}, Password length: ${request.password.length}")

                // Валидация
                if (request.phoneNumber.isBlank()) {
                    println("ERROR: Phone number is blank")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Phone number is required")
                    )
                    return@post
                }

                if (request.password.length < 8) {
                    println("ERROR: Password too short")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Password must be at least 8 characters")
                    )
                    return@post
                }

                // Проверяем существование пользователя
                println("Checking if user exists...")
                val existingUser = userDao.getUserByPhone(request.phoneNumber)
                if (existingUser != null) {
                    println("ERROR: User already exists with id: ${existingUser.id}")
                    call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse(error = "User with this phone number already exists")
                    )
                    return@post
                }
                println("User does not exist, creating new user...")

                // Создаём пользователя
                val newUser = userDao.createUser(request.phoneNumber, request.password)
                println("createUser returned: $newUser")
                if (newUser == null) {
                    println("ERROR: createUser returned null")
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Failed to create user")
                    )
                    return@post
                }

                println("SUCCESS: User created with id: ${newUser.id}")
                // Возвращаем успех с кодом верификации (для демо)
                call.respond(
                    HttpStatusCode.Created,
                    AuthResponse(
                        success = true,
                        message = "User registered successfully. Verification code: ${newUser.verificationCode}",
                        user = UserResponse(
                            id = newUser.id,
                            phoneNumber = newUser.phoneNumber,
                            isPhoneVerified = newUser.isPhoneVerified,
                            createdAt = newUser.createdAt.toString(),
                            profile = null
                        )
                    )
                )
                println("=== REGISTER RESPONSE SENT ===")

            } catch (e: Exception) {
                println("!!! EXCEPTION in /register: ${e::class.simpleName} - ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }

        // ============= LOGIN ENDPOINT =============
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()

                // Валидация
                if (request.phoneNumber.isBlank() || request.password.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Phone number and password are required")
                    )
                    return@post
                }

                // Проверяем существование пользователя
                val user = userDao.getUserByPhone(request.phoneNumber)
                if (user == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(error = "User not found")
                    )
                    return@post
                }

                // Проверяем верификацию телефона
                if (!user.isPhoneVerified) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        ErrorResponse(error = "Phone number not verified")
                    )
                    return@post
                }

                // Проверяем пароль
                val passwordCorrect = userDao.verifyPassword(request.phoneNumber, request.password)
                if (!passwordCorrect) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(error = "Invalid password")
                    )
                    return@post
                }

                // Обновляем last_login_at
                userDao.updateLastLogin(request.phoneNumber)

                // Получаем профиль (если есть)
                val profile = userDao.getProfileByUserId(user.id)

                // Возвращаем успех
                call.respond(
                    HttpStatusCode.OK,
                    AuthResponse(
                        success = true,
                        message = "Login successful",
                        user = UserResponse(
                            id = user.id,
                            phoneNumber = user.phoneNumber,
                            isPhoneVerified = user.isPhoneVerified,
                            createdAt = user.createdAt.toString(),
                            profile = profile?.let {
                                ProfileResponse(
                                    id = it.id,
                                    fullName = it.fullName,
                                    gender = it.gender,
                                    birthDate = it.birthDate?.toString(),
                                    height = it.height,
                                    weight = it.weight,
                                    region = it.region
                                )
                            }
                        )
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }

        // ============= VERIFY CODE ENDPOINT =============
        post("/verify") {
            try {
                val request = call.receive<VerifyCodeRequest>()

                // Валидация
                if (request.phoneNumber.isBlank() || request.code.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Phone number and code are required")
                    )
                    return@post
                }

                // Проверяем существование пользователя
                val user = userDao.getUserByPhone(request.phoneNumber)
                if (user == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(error = "User not found")
                    )
                    return@post
                }

                // Проверяем уже верифицирован ли
                if (user.isPhoneVerified) {
                    call.respond(
                        HttpStatusCode.OK,
                        AuthResponse(
                            success = true,
                            message = "Phone already verified",
                            user = UserResponse(
                                id = user.id,
                                phoneNumber = user.phoneNumber,
                                isPhoneVerified = true,
                                createdAt = user.createdAt.toString(),
                                profile = null
                            )
                        )
                    )
                    return@post
                }

                // Проверяем код
                if (user.verificationCode != request.code) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Invalid verification code")
                    )
                    return@post
                }

                // Проверяем срок действия кода
                if (user.verificationCodeExpiry != null &&
                    user.verificationCodeExpiry < java.time.LocalDateTime.now()
                ) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Verification code expired")
                    )
                    return@post
                }

                // Верифицируем телефон
                val verified = userDao.verifyPhone(request.phoneNumber)
                if (!verified) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Failed to verify phone")
                    )
                    return@post
                }

                // Получаем обновлённого пользователя
                val updatedUser = userDao.getUserByPhone(request.phoneNumber)!!

                call.respond(
                    HttpStatusCode.OK,
                    AuthResponse(
                        success = true,
                        message = "Phone verified successfully",
                        user = UserResponse(
                            id = updatedUser.id,
                            phoneNumber = updatedUser.phoneNumber,
                            isPhoneVerified = updatedUser.isPhoneVerified,
                            createdAt = updatedUser.createdAt.toString(),
                            profile = null
                        )
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }
    }
}