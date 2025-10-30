package com.lifevault

import com.lifevault.database.DatabaseFactory
import com.lifevault.routes.authRoutes
import com.lifevault.routes.profileRoutes
import com.lifevault.routes.testRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import com.typesafe.config.ConfigFactory

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        configure = {
            // Load application.conf
            val appConfig = ConfigFactory.load()
        }
    ) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // Инициализируем подключение к базе данных
    DatabaseFactory.init()

    // ============= PLUGINS =============

    // Сериализация JSON
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // CORS (для локальной разработки)
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // Для продакшена нужно ограничить конкретными хостами!
    }

    // Обработка ошибок
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val errorMessage = cause.message?.replace("\"", "\\\"") ?: "Unknown error"
            call.respondText(
                """{"error":"$errorMessage"}""",
                ContentType.Application.Json,
                HttpStatusCode.InternalServerError
            )
        }
    }

    // ============= ROUTING =============
    routing {
        // Health check endpoint
        get("/") {
            call.respondText(
                """{"status":"UP","message":"LifeVault Backend API","version":"0.0.1"}""",
                ContentType.Application.Json
            )
        }

        get("/health") {
            call.respondText(
                """{"status":"healthy","timestamp":${System.currentTimeMillis()}}""",
                ContentType.Application.Json
            )
        }

        // Auth routes
        authRoutes()

        // Profile routes
        profileRoutes()

        // Test routes (для E2E тестов)
        // ВАЖНО: В production эти маршруты должны быть отключены!
        testRoutes()
    }
}