package com.lifevault.routes

import com.lifevault.database.UserDao
import com.lifevault.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

fun Route.profileRoutes() {
    val userDao = UserDao()

    route("/api/profile") {

        // ============= CREATE PROFILE ENDPOINT =============
        post {
            try {
                val request = call.receive<CreateProfileRequest>()

                // Валидация
                if (request.userId <= 0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Invalid user ID")
                    )
                    return@post
                }

                // Проверяем существование пользователя
                val user = userDao.getUserById(request.userId)
                if (user == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(error = "User not found")
                    )
                    return@post
                }

                // Проверяем, нет ли уже профиля
                val existingProfile = userDao.getProfileByUserId(request.userId)
                if (existingProfile != null) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse(error = "Profile already exists for this user")
                    )
                    return@post
                }

                // Парсим дату рождения
                val birthDate = request.birthDate?.let { LocalDate.parse(it) }

                // Создаём профиль
                val profile = userDao.createProfile(
                    userId = request.userId,
                    fullName = request.fullName,
                    gender = request.gender,
                    birthDate = birthDate,
                    height = request.height,
                    weight = request.weight,
                    region = request.region
                )

                if (profile == null) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Failed to create profile")
                    )
                    return@post
                }

                call.respond(
                    HttpStatusCode.Created,
                    ProfileResponse(
                        id = profile.id,
                        fullName = profile.fullName,
                        gender = profile.gender,
                        birthDate = profile.birthDate?.toString(),
                        height = profile.height,
                        weight = profile.weight,
                        region = profile.region
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }

        // ============= GET PROFILE BY USER ID ENDPOINT =============
        get("/{userId}") {
            try {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Invalid user ID")
                    )
                    return@get
                }

                val profile = userDao.getProfileByUserId(userId)
                if (profile == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(error = "Profile not found")
                    )
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    ProfileResponse(
                        id = profile.id,
                        fullName = profile.fullName,
                        gender = profile.gender,
                        birthDate = profile.birthDate?.toString(),
                        height = profile.height,
                        weight = profile.weight,
                        region = profile.region
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }

        // ============= UPDATE PROFILE ENDPOINT =============
        put("/{userId}") {
            try {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Invalid user ID")
                    )
                    return@put
                }

                val request = call.receive<UpdateProfileRequest>()

                // Проверяем существование профиля
                val existingProfile = userDao.getProfileByUserId(userId)
                if (existingProfile == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(error = "Profile not found")
                    )
                    return@put
                }

                // Парсим дату рождения
                val birthDate = request.birthDate?.let { LocalDate.parse(it) }

                // Обновляем профиль
                val updated = userDao.updateProfile(
                    userId = userId,
                    fullName = request.fullName,
                    gender = request.gender,
                    birthDate = birthDate,
                    height = request.height,
                    weight = request.weight,
                    region = request.region
                )

                if (!updated) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(error = "Failed to update profile")
                    )
                    return@put
                }

                // Получаем обновлённый профиль
                val updatedProfile = userDao.getProfileByUserId(userId)!!

                call.respond(
                    HttpStatusCode.OK,
                    ProfileResponse(
                        id = updatedProfile.id,
                        fullName = updatedProfile.fullName,
                        gender = updatedProfile.gender,
                        birthDate = updatedProfile.birthDate?.toString(),
                        height = updatedProfile.height,
                        weight = updatedProfile.weight,
                        region = updatedProfile.region
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