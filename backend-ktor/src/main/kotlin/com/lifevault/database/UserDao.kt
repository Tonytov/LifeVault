package com.lifevault.database

import com.lifevault.models.User
import com.lifevault.models.UserProfile
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.date
import java.security.MessageDigest
import java.time.LocalDateTime

// ============= TABLE DEFINITIONS =============

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val phoneNumber = varchar("phone_number", 20).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val isPhoneVerified = bool("is_phone_verified").default(false)
    val verificationCode = varchar("verification_code", 6).nullable()
    val verificationCodeExpiry = datetime("verification_code_expiry").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val lastLoginAt = datetime("last_login_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

object UserProfiles : Table("user_profiles") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val fullName = varchar("full_name", 100).nullable()
    val gender = varchar("gender", 10).nullable()
    val birthDate = date("birth_date").nullable()
    val height = integer("height").nullable()
    val weight = integer("weight").nullable()
    val region = varchar("region", 50).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}

// ============= DATA ACCESS OBJECT =============

class UserDao {

    // Хеширование пароля
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    // ============= USER OPERATIONS =============

    suspend fun createUser(phoneNumber: String, password: String): User? = DatabaseFactory.dbQuery {
        val insertStatement = Users.insert {
            it[Users.phoneNumber] = phoneNumber
            it[passwordHash] = hashPassword(password)
            it[isPhoneVerified] = false
            it[verificationCode] = "111111" // Для демо всегда один код
            it[verificationCodeExpiry] = LocalDateTime.now().plusMinutes(5)
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToUser)
    }

    suspend fun getUserByPhone(phoneNumber: String): User? = DatabaseFactory.dbQuery {
        Users.select { Users.phoneNumber eq phoneNumber }
            .mapNotNull(::rowToUser)
            .singleOrNull()
    }

    suspend fun getUserById(id: Int): User? = DatabaseFactory.dbQuery {
        Users.select { Users.id eq id }
            .mapNotNull(::rowToUser)
            .singleOrNull()
    }

    suspend fun verifyPhone(phoneNumber: String): Boolean = DatabaseFactory.dbQuery {
        Users.update({ Users.phoneNumber eq phoneNumber }) {
            it[isPhoneVerified] = true
            it[verificationCode] = null
            it[verificationCodeExpiry] = null
        } > 0
    }

    suspend fun updateLastLogin(phoneNumber: String): Boolean = DatabaseFactory.dbQuery {
        Users.update({ Users.phoneNumber eq phoneNumber }) {
            it[lastLoginAt] = LocalDateTime.now()
        } > 0
    }

    suspend fun verifyPassword(phoneNumber: String, password: String): Boolean = DatabaseFactory.dbQuery {
        val user = Users.select { Users.phoneNumber eq phoneNumber }
            .mapNotNull(::rowToUser)
            .singleOrNull()

        user?.passwordHash == hashPassword(password)
    }

    // ============= TEST CLEANUP OPERATIONS =============

    /**
     * Удаляет пользователя по номеру телефона.
     * Используется для очистки тестовых данных.
     */
    suspend fun deleteUserByPhone(phoneNumber: String): Boolean = DatabaseFactory.dbQuery {
        Users.deleteWhere { Users.phoneNumber eq phoneNumber } > 0
    }

    /**
     * Удаляет всех пользователей с номерами телефонов, начинающимися с указанного префикса.
     * Полезно для массовой очистки тестовых данных.
     */
    suspend fun deleteUsersByPhonePrefix(prefix: String): Int = DatabaseFactory.dbQuery {
        Users.deleteWhere { Users.phoneNumber like "$prefix%" }
    }

    /**
     * Удаляет всех пользователей, созданных после указанного времени.
     * Полезно для очистки только новых тестовых данных.
     */
    suspend fun deleteUsersCreatedAfter(dateTime: LocalDateTime): Int = DatabaseFactory.dbQuery {
        Users.deleteWhere { Users.createdAt greater dateTime }
    }

    /**
     * Получает список всех пользователей (для отладки).
     */
    suspend fun getAllUsers(): List<User> = DatabaseFactory.dbQuery {
        Users.selectAll().map(::rowToUser)
    }

    // ============= PROFILE OPERATIONS =============

    suspend fun getProfileByUserId(userId: Int): UserProfile? = DatabaseFactory.dbQuery {
        UserProfiles.select { UserProfiles.userId eq userId }
            .mapNotNull(::rowToProfile)
            .singleOrNull()
    }

    suspend fun createProfile(
        userId: Int,
        fullName: String?,
        gender: String?,
        birthDate: java.time.LocalDate?,
        height: Int?,
        weight: Int?,
        region: String?
    ): UserProfile? = DatabaseFactory.dbQuery {
        val insertStatement = UserProfiles.insert {
            it[UserProfiles.userId] = userId
            it[UserProfiles.fullName] = fullName
            it[UserProfiles.gender] = gender
            it[UserProfiles.birthDate] = birthDate
            it[UserProfiles.height] = height
            it[UserProfiles.weight] = weight
            it[UserProfiles.region] = region
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToProfile)
    }

    suspend fun updateProfile(
        userId: Int,
        fullName: String?,
        gender: String?,
        birthDate: java.time.LocalDate?,
        height: Int?,
        weight: Int?,
        region: String?
    ): Boolean = DatabaseFactory.dbQuery {
        UserProfiles.update({ UserProfiles.userId eq userId }) {
            it[UserProfiles.fullName] = fullName
            it[UserProfiles.gender] = gender
            it[UserProfiles.birthDate] = birthDate
            it[UserProfiles.height] = height
            it[UserProfiles.weight] = weight
            it[UserProfiles.region] = region
        } > 0
    }

    // ============= ROW MAPPERS =============

    private fun rowToUser(row: ResultRow): User = User(
        id = row[Users.id],
        phoneNumber = row[Users.phoneNumber],
        passwordHash = row[Users.passwordHash],
        isPhoneVerified = row[Users.isPhoneVerified],
        verificationCode = row[Users.verificationCode],
        verificationCodeExpiry = row[Users.verificationCodeExpiry],
        createdAt = row[Users.createdAt],
        lastLoginAt = row[Users.lastLoginAt]
    )

    private fun rowToProfile(row: ResultRow): UserProfile = UserProfile(
        id = row[UserProfiles.id],
        userId = row[UserProfiles.userId],
        fullName = row[UserProfiles.fullName],
        gender = row[UserProfiles.gender],
        birthDate = row[UserProfiles.birthDate],
        height = row[UserProfiles.height],
        weight = row[UserProfiles.weight],
        region = row[UserProfiles.region],
        createdAt = row[UserProfiles.createdAt]
    )
}