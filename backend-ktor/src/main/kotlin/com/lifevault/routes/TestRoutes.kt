package com.lifevault.routes

import com.lifevault.database.UserDao
import com.lifevault.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserRequest(
    val phoneNumber: String
)

@Serializable
data class DeleteByPrefixRequest(
    val prefix: String
)

@Serializable
data class TestCleanupResponse(
    val success: Boolean,
    val message: String,
    val deletedCount: Int = 0
)

/**
 * Test-only routes для очистки тестовых данных.
 *
 * ВАЖНО: Эти эндпоинты НЕ должны быть доступны в production!
 */
fun Route.testRoutes() {
    val userDao = UserDao()

    route("/api/test") {

        // ============= DELETE USER BY PHONE =============
        post("/deleteUser") {
            try {
                val request = call.receive<DeleteUserRequest>()

                if (request.phoneNumber.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Phone number is required")
                    )
                    return@post
                }

                val deleted = userDao.deleteUserByPhone(request.phoneNumber)

                call.respond(
                    HttpStatusCode.OK,
                    TestCleanupResponse(
                        success = deleted,
                        message = if (deleted) "User deleted successfully" else "User not found",
                        deletedCount = if (deleted) 1 else 0
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }

        // ============= DELETE USERS BY PREFIX =============
        post("/deleteByPrefix") {
            try {
                val request = call.receive<DeleteByPrefixRequest>()

                if (request.prefix.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(error = "Prefix is required")
                    )
                    return@post
                }

                val deletedCount = userDao.deleteUsersByPhonePrefix(request.prefix)

                call.respond(
                    HttpStatusCode.OK,
                    TestCleanupResponse(
                        success = true,
                        message = "Deleted $deletedCount users with prefix '${request.prefix}'",
                        deletedCount = deletedCount
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }

        // ============= GET ALL USERS (DEBUG) =============
        get("/users") {
            try {
                val users = userDao.getAllUsers()

                val userList = users.map { user ->
                    mapOf(
                        "id" to user.id,
                        "phoneNumber" to user.phoneNumber,
                        "isPhoneVerified" to user.isPhoneVerified,
                        "createdAt" to user.createdAt.toString()
                    )
                }

                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "success" to true,
                        "count" to users.size,
                        "users" to userList
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse(error = "Internal server error: ${e.message}")
                )
            }
        }

        // ============= HEALTH CHECK FOR TESTS =============
        get("/health") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "status" to "ok",
                    "message" to "Test endpoints are available"
                )
            )
        }
    }
}