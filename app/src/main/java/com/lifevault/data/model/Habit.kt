package com.lifevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: HabitType,
    val quitDate: LocalDateTime? = null,
    val isActive: Boolean = true,
    val dailyUsage: Int? = null, // основное потребление в день
    val weeklyUsage: Int? = null, // для алкоголя - единиц в неделю
    val costPerUnit: Double = 0.0, // цена за единицу
    val startedDate: LocalDateTime? = null, // когда началась привычка
    val intensity: HabitIntensity = HabitIntensity.MODERATE,
    val triggers: List<String> = emptyList(), // триггеры для привычки
    val notes: String? = null, // дополнительные заметки
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class HabitType(
    val displayName: String,
    val lifeYearsLost: Int, // years lost if having this habit
    val lifeYearsGained: Int, // years gained when quitting
    val unit: String,
    val icon: String,
    val detailedUnits: Map<String, String> // детальные единицы измерения
) {
    SMOKING(
        "Курение", 10, 10, "сигарет/день", "🚬",
        mapOf(
            "cigarettes_per_day" to "сигарет в день",
            "packs_per_day" to "пачек в день",
            "years_smoking" to "лет курения"
        )
    ),
    ALCOHOL(
        "Алкоголь", 5, 5, "единиц/неделю", "🍺",
        mapOf(
            "units_per_week" to "единиц в неделю",
            "beer_per_week" to "пива в неделю (л)",
            "wine_per_week" to "вина в неделю (бутылок)",
            "spirits_per_week" to "крепких напитков в неделю (мл)"
        )
    ),
    SUGAR(
        "Сахар", 3, 3, "г/день", "🍯",
        mapOf(
            "added_sugar_per_day" to "добавленного сахара в день (г)",
            "desserts_per_week" to "десертов в неделю",
            "sweetened_drinks_per_day" to "сладких напитков в день"
        )
    ),
}

enum class HabitIntensity(val displayName: String, val multiplier: Double) {
    LIGHT("Легкая", 0.5),
    MODERATE("Умеренная", 1.0),
    HEAVY("Сильная", 1.5),
    SEVERE("Критическая", 2.0)
}

data class HabitProgress(
    val habit: Habit,
    val daysSinceQuit: Long,
    val lifeYearsRecovered: Double,
    val moneySaved: Double,
    val unitsAvoided: Int
)