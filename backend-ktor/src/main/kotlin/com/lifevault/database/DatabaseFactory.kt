package com.lifevault.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {

    fun init() {
        // Development configuration (hardcoded)
        val driver = "org.postgresql.Driver"
        val url = "jdbc:postgresql://localhost:5432/lifevault_db"
        val user = "lifevault_user"
        val password = "lifevault_password"
        val maxPoolSize = 10

        Database.connect(createHikariDataSource(url, driver, user, password, maxPoolSize))
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        password: String,
        maxPoolSize: Int
    ): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            username = user
            this.password = password
            maximumPoolSize = maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}