package com.lifevault.data.model

import java.time.LocalDate

/**
 * Дневная сводка по привычке для отображения на главном экране
 */
data class TodayHabitSummary(
    val habitType: HabitType,
    val totalAmount: Int,                    // всего за сегодня (5 сигарет, 3 единицы алкоголя, 40г сахара)
    val limit: Int,                          // дневная норма
    val percentage: Float,                   // процент от нормы
    val statusColor: HabitStatusColor,       // цвет статуса (градиент)
    val entries: List<HabitEntry> = emptyList(),  // список событий за сегодня
    val streak: Int = 0,                     // серия дней без привычки (для алкоголя/курения)
    val hasExceededLimit: Boolean = false    // превышена ли норма
) {
    /**
     * Текст статуса для отображения
     */
    fun getStatusText(): String {
        return when (habitType) {
            HabitType.SMOKING -> {
                when {
                    totalAmount == 0 -> "Не курили сегодня"
                    totalAmount <= limit -> "${statusColor.emoji} ${percentage.toInt()}% от цели"
                    else -> "${statusColor.emoji} Превышена норма на ${totalAmount - limit}"
                }
            }
            HabitType.ALCOHOL -> {
                when {
                    totalAmount == 0 && streak > 0 -> "🟢 Не пил сегодня\n🔥 Серия: $streak ${getDaysWord(streak)}"
                    totalAmount == 0 -> "🟢 Не пил сегодня"
                    totalAmount <= limit -> "${statusColor.emoji} ${percentage.toInt()}% от недельной нормы"
                    else -> "${statusColor.emoji} Превышена норма на ${totalAmount - limit} единиц"
                }
            }
            HabitType.SUGAR -> {
                when {
                    totalAmount == 0 -> "Не употребляли сахар"
                    totalAmount <= limit -> "${statusColor.emoji} В пределах нормы"
                    else -> "${statusColor.emoji} Превышена норма на ${totalAmount - limit}г"
                }
            }
        }
    }

    private fun getDaysWord(days: Int): String {
        return when {
            days % 10 == 1 && days % 100 != 11 -> "день"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
            else -> "дней"
        }
    }

    /**
     * Процент заполнения прогресс-бара (для сахара)
     */
    fun getProgressPercentage(): Float {
        return (totalAmount.toFloat() / limit.coerceAtLeast(1)).coerceIn(0f, 1f)
    }
}

/**
 * UI состояние главного экрана "Сегодня"
 */
data class TodayUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val date: LocalDate = LocalDate.now(),

    // Привычки
    val smokingSummary: TodayHabitSummary? = null,
    val alcoholSummary: TodayHabitSummary? = null,
    val sugarSummary: TodayHabitSummary? = null,

    // Прогноз жизни (две линейки)
    val lifeExpectancyBefore: Double = 75.8,     // прогноз до изменения привычек
    val lifeExpectancyCurrent: Double = 78.4,    // текущий прогноз
    val yearsGained: Double = 2.6,               // сколько лет добавлено
    val daysGainedThisMonth: Int = 42,           // дней добавлено за месяц

    // Достижения (последние 2-3)
    val recentAchievements: List<Achievement> = emptyList(),

    // Научные статьи (2-3 для карусели)
    val featuredArticles: List<Article> = emptyList(),

    // Time Bank (интегрирован во вкладку Прогресс)
    val timeBankBalance: Int = 0,

    val error: String? = null,

    // События для UI
    val event: HabitUIEvent? = null
)

/**
 * Событие изменения состояния привычки
 */
sealed class HabitEvent {
    data class AddEntry(val entry: HabitEntry) : HabitEvent()
    data class DeleteEntry(val entryId: Long) : HabitEvent()
    data class UpdateEntry(val entry: HabitEntry) : HabitEvent()
    object RefreshData : HabitEvent()
}

/**
 * UI события для отображения уведомлений/сообщений
 */
sealed class HabitUIEvent {
    data class EntryAdded(val habitType: HabitType) : HabitUIEvent()
    data class LimitExceeded(val habitType: HabitType, val current: Int, val limit: Int) : HabitUIEvent()
    data class StreakAchieved(val habitType: HabitType, val days: Int) : HabitUIEvent()
}

/**
 * Состояние развернутой карточки привычки
 */
data class ExpandedHabitCardState(
    val habitType: HabitType,
    val isExpanded: Boolean = false
)