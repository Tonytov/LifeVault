package helpers

import kotlin.random.Random

/**
 * Генератор уникальных тестовых данных для E2E тестов.
 *
 * Предотвращает конфликты при повторных запусках тестов.
 */
object TestUserGenerator {

    /**
     * Генерирует уникальный номер телефона на основе timestamp.
     * Формат: 7XXXXXXXXXX (11 цифр, начинается с 7)
     *
     * Пример: 75432198765
     */
    fun generateUniquePhoneNumber(): String {
        // Используем timestamp для уникальности
        val timestamp = System.currentTimeMillis() % 9000000000 + 1000000000
        return "7$timestamp"
    }

    /**
     * Генерирует уникальный номер телефона с префиксом для удобства отладки.
     *
     * @param prefix Префикс для идентификации теста (например, "login", "reg")
     * @return Номер телефона формата 7XXXXXXXXXX
     */
    fun generatePhoneNumberWithPrefix(prefix: String = ""): String {
        val baseNumber = System.currentTimeMillis() % 900000000 + 100000000
        return "7${prefix.take(1)}$baseNumber"
    }

    /**
     * Генерирует случайный номер телефона для негативных тестов.
     */
    fun generateRandomPhoneNumber(): String {
        val random = Random.nextLong(1000000000, 9999999999)
        return "7$random"
    }

    /**
     * Стандартный тестовый пароль.
     */
    const val DEFAULT_TEST_PASSWORD = "test1111"

    /**
     * Генерирует уникальный email (опционально для будущего).
     */
    fun generateUniqueEmail(): String {
        val timestamp = System.currentTimeMillis()
        return "test_$timestamp@lifevault.test"
    }

    /**
     * Генерирует тестовое имя пользователя.
     */
    fun generateUserName(): String {
        val names = listOf("Иван", "Петр", "Мария", "Анна", "Алексей")
        val surnames = listOf("Иванов", "Петров", "Сидоров", "Козлов", "Смирнов")
        return "${names.random()} ${surnames.random()}"
    }
}
