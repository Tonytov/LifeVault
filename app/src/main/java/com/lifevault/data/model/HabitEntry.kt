package com.lifevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDateTime

/**
 * Запись события привычки (например: выкурил сигарету, выпил пиво)
 * Используется для детального трекинга в течение дня
 */
@Entity(
    tableName = "habit_entries",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId"), Index("timestamp")]
)
data class HabitEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long? = null,            // связь с Habit (nullable для записей без привязки)
    val habitType: HabitType,             // SMOKING, ALCOHOL, SUGAR
    val timestamp: LocalDateTime,         // когда произошло событие
    val amount: Int,                      // количество (1 сигарета, 2 единицы алкоголя, 25г сахара)
    val preset: String? = null,           // пресет: "after_coffee", "stress", "beer_small", "chocolate_bar"
    val note: String? = null,             // заметка пользователя
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Пресеты для быстрой отметки курения
 */
enum class SmokingPreset(
    val key: String,
    val emoji: String,
    val displayName: String,
    val amount: Int = 1
) {
    MORNING("morning", "☀️", "Утренняя", 1),
    AFTER_COFFEE("after_coffee", "☕", "После кофе", 1),
    AFTER_MEAL("after_meal", "🍽️", "После еды", 1),
    STRESS("stress", "😰", "Стресс", 1),
    SOCIAL("social", "📱", "Компания", 1),
    BOREDOM("boredom", "😑", "Скука", 1),
    BREAK("break", "⏸️", "Перерыв", 1),
    WITH_ALCOHOL("with_alcohol", "🍺", "С алкоголем", 1),
    OTHER("other", "➕", "Другое", 1);

    companion object {
        fun fromKey(key: String?): SmokingPreset? = values().find { it.key == key }
    }
}

/**
 * Пресеты для быстрой отметки алкоголя
 */
enum class AlcoholPreset(
    val key: String,
    val emoji: String,
    val displayName: String,
    val standardUnits: Double  // стандартных единиц алкоголя
) {
    BEER_SMALL("beer_small", "🍺", "Пиво (0.33л)", 1.3),
    BEER_LARGE("beer_large", "🍺", "Пиво (0.5л)", 2.0),
    WINE_GLASS("wine_glass", "🍷", "Бокал вина", 1.4),
    WINE_BOTTLE("wine_bottle", "🍷", "Бутылка вина", 7.0),
    CHAMPAGNE("champagne", "🍾", "Шампанское", 1.2),
    VODKA_SHOT("vodka_shot", "🥃", "Водка (50мл)", 1.6),
    WHISKEY("whiskey", "🥃", "Виски", 1.6),
    COCKTAIL("cocktail", "🍹", "Коктейль", 2.0),
    OTHER("other", "➕", "Другое", 1.0);

    companion object {
        fun fromKey(key: String?): AlcoholPreset? = values().find { it.key == key }
    }
}

/**
 * Пресеты для быстрой отметки сахара
 */
enum class SugarPreset(
    val key: String,
    val emoji: String,
    val displayName: String,
    val sugarGrams: Int  // граммов сахара
) {
    CHOCOLATE_BAR("chocolate_bar", "🍫", "Шоколад (50г)", 25),
    CHOCOLATE_SMALL("chocolate_small", "🍫", "Маленький шоколад", 10),
    SODA_CAN("soda_can", "🥤", "Газировка (330мл)", 35),
    SODA_LARGE("soda_large", "🥤", "Газировка (0.5л)", 50),
    JUICE_GLASS("juice_glass", "🧃", "Сок (стакан)", 20),
    COOKIES_3("cookies_3", "🍪", "Печенье (3 шт)", 15),
    CAKE_SLICE("cake_slice", "🍰", "Кусок торта", 30),
    ICE_CREAM("ice_cream", "🍨", "Мороженое", 20),
    YOGURT_SWEET("yogurt_sweet", "🥛", "Йогурт сладкий", 12),
    CANDY("candy", "🍬", "Конфета", 5),
    DONUT("donut", "🍩", "Пончик", 15),
    COFFEE_SWEET("coffee_sweet", "☕", "Кофе с сахаром", 10),
    ENERGY_DRINK("energy_drink", "⚡", "Энергетик", 40),
    OTHER("other", "➕", "Другое", 10);

    companion object {
        fun fromKey(key: String?): SugarPreset? = values().find { it.key == key }
    }
}

/**
 * Дневные нормы ВОЗ
 */
object WHOLimits {
    // Курение
    const val SMOKING_DAILY_LIMIT = 0  // ВОЗ: безопасной дозы нет
    const val SMOKING_TARGET_REDUCTION = 5  // целевое снижение для активных курильщиков

    // Алкоголь
    const val ALCOHOL_WEEKLY_LIMIT_MALE = 14.0  // стандартных единиц в неделю
    const val ALCOHOL_WEEKLY_LIMIT_FEMALE = 11.0
    const val ALCOHOL_DAILY_LIMIT_MALE = 2.0
    const val ALCOHOL_DAILY_LIMIT_FEMALE = 1.5

    // Сахар (добавленный)
    const val SUGAR_DAILY_LIMIT = 50  // граммов в день
    const val SUGAR_DAILY_STRICT_LIMIT = 25  // строгая норма ВОЗ
}

/**
 * Градиентная цветовая схема для статуса привычки
 */
enum class HabitStatusColor(
    val colorHex: Long,
    val emoji: String,
    val label: String
) {
    EXCELLENT(0xFF2ECC71, "🟢", "Отлично"),      // 0-25% от нормы
    GOOD(0xFF27AE60, "🟢", "Хорошо"),           // 26-50%
    MODERATE(0xFFF39C12, "🟡", "Умеренно"),     // 51-75%
    CAUTION(0xFFE67E22, "🟠", "Осторожно"),     // 76-100%
    EXCEEDED(0xFFE74C3C, "🔴", "Превышено");    // >100%

    companion object {
        /**
         * Определить статус на основе процента от нормы
         */
        fun fromPercentage(percentage: Float): HabitStatusColor {
            return when {
                percentage <= 25f -> EXCELLENT
                percentage <= 50f -> GOOD
                percentage <= 75f -> MODERATE
                percentage <= 100f -> CAUTION
                else -> EXCEEDED
            }
        }

        /**
         * Определить статус на основе текущего и лимитного значений
         */
        fun fromValues(current: Int, limit: Int): HabitStatusColor {
            if (limit <= 0) return EXCELLENT  // если лимит 0, любое значение превышение
            val percentage = (current.toFloat() / limit) * 100f
            return fromPercentage(percentage)
        }
    }
}
