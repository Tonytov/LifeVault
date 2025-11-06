package helpers

import kotlin.random.Random

/**
 * Генератор уникальных тестовых данных для E2E тестов.
 *
 * Предотвращает конфликты при повторных запусках тестов.
 */
object TestUserGenerator {

    /**
     * Генерирует уникальный номер телефона в формате +79XXXXXXXXX.
     *
     * @return Номер телефона из 11 цифр: +7 9 и 9 случайных цифр
     *
     * Пример: +79123456789
     */
    fun generateUniquePhoneNumber(): String {
        // Генерируем 9 случайных цифр
        val randomDigits = (1..9).joinToString("") { Random.nextInt(0, 10).toString() }
        return "+79$randomDigits"
    }

    /**
     * Генерирует уникальный пароль длиной 8 символов.
     * Гарантированно содержит минимум одну цифру.
     *
     * @return Пароль длиной 8 символов
     *
     * Пример: aB3d5Fg8
     */
    fun generateUniquePassword(): String {
        val letters = ('a'..'z') + ('A'..'Z')
        val digits = ('0'..'9')

        // Генерируем пароль: минимум 1 цифра + 7 остальных символов
        val password = buildString {
            // Добавляем хотя бы одну цифру
            append(digits.random())

            // Добавляем оставшиеся 7 символов из букв и цифр
            val allChars = letters + digits
            repeat(7) {
                append(allChars.random())
            }
        }

        // Перемешиваем символы, чтобы цифра не всегда была первой
        return password.toList().shuffled().joinToString("")
    }
}
