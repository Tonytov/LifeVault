package com.lifevault.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifevault.data.dao.HabitEntryDao
import com.lifevault.data.model.*
import com.lifevault.data.repository.LifeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * ViewModel для экрана "Сегодня"
 * Управляет состоянием главного экрана с отслеживанием привычек
 */
class TodayViewModel(
    private val habitEntryDao: HabitEntryDao,
    private val lifeRepository: LifeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init {
        loadTodayData()
    }

    /**
     * Загрузить все данные для главного экрана
     */
    fun loadTodayData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val today = LocalDate.now()
                val dateString = today.toString()

                // Загрузить профиль пользователя для расчета продолжительности жизни
                val (profile, habits) = lifeRepository.getUserProfileWithHabits().first()

                // Загрузить данные по каждой привычке
                val smokingSummary = loadHabitSummary(HabitType.SMOKING, dateString)
                val alcoholSummary = loadHabitSummary(HabitType.ALCOHOL, dateString)
                val sugarSummary = loadHabitSummary(HabitType.SUGAR, dateString)

                // Расчет продолжительности жизни
                val (lifeExpectancyBefore, lifeExpectancyCurrent, yearsGained) =
                    calculateLifeExpectancy(profile, habits, smokingSummary, alcoholSummary, sugarSummary)

                // Загрузить достижения (пока заглушка)
                val achievements = loadRecentAchievements()

                // Загрузить статьи (пока заглушка)
                val articles = loadFeaturedArticles()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    smokingSummary = smokingSummary,
                    alcoholSummary = alcoholSummary,
                    sugarSummary = sugarSummary,
                    lifeExpectancyBefore = lifeExpectancyBefore,
                    lifeExpectancyCurrent = lifeExpectancyCurrent,
                    yearsGained = yearsGained,
                    daysGainedThisMonth = calculateDaysGainedThisMonth(),
                    recentAchievements = achievements,
                    featuredArticles = articles
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Загрузить сводку по конкретной привычке за сегодня
     */
    private suspend fun loadHabitSummary(habitType: HabitType, dateString: String): TodayHabitSummary {
        val entries = habitEntryDao.getEntriesForDay(habitType, dateString)
        val totalAmount = habitEntryDao.getTotalForDay(habitType, dateString)
        val limit = when (habitType) {
            HabitType.SMOKING -> WHOLimits.SMOKING_DAILY_LIMIT
            HabitType.ALCOHOL -> WHOLimits.ALCOHOL_DAILY_LIMIT_MALE.toInt()
            HabitType.SUGAR -> WHOLimits.SUGAR_DAILY_LIMIT
        }

        val percentage = if (limit > 0) (totalAmount.toFloat() / limit) * 100 else 0f
        val statusColor = HabitStatusColor.fromPercentage(percentage)
        val hasExceededLimit = totalAmount > limit
        val streak = 0 // TODO: Implement streak calculation

        return TodayHabitSummary(
            habitType = habitType,
            totalAmount = totalAmount,
            limit = limit,
            percentage = percentage,
            statusColor = statusColor,
            entries = entries,
            streak = streak,
            hasExceededLimit = hasExceededLimit
        )
    }

    /**
     * Добавить запись о привычке (быстрое добавление)
     */
    fun quickAddEntry(habitType: HabitType, amount: Int = 1, note: String? = null) {
        viewModelScope.launch {
            val entry = HabitEntry(
                habitId = null, // Не привязываем к конкретной Habit
                habitType = habitType,
                timestamp = LocalDateTime.now(),
                amount = amount,
                preset = null,
                note = note
            )

            habitEntryDao.insertEntry(entry)

            // Обновить данные после добавления
            loadTodayData()

            // Проверить, превышен ли лимит, и показать соответствующее событие
            val today = LocalDate.now().toString()
            val totalToday = habitEntryDao.getTotalForDay(habitType, today)
            val limit = when (habitType) {
                HabitType.SMOKING -> WHOLimits.SMOKING_DAILY_LIMIT
                HabitType.ALCOHOL -> WHOLimits.ALCOHOL_DAILY_LIMIT_MALE.toInt()
                HabitType.SUGAR -> WHOLimits.SUGAR_DAILY_LIMIT
            }

            if (totalToday > limit) {
                _uiState.value = _uiState.value.copy(
                    event = HabitUIEvent.LimitExceeded(habitType, totalToday, limit)
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    event = HabitUIEvent.EntryAdded(habitType)
                )
            }
        }
    }

    /**
     * Добавить запись с пресетом
     */
    fun addPresetEntry(habitType: HabitType, presetKey: String) {
        viewModelScope.launch {
            val (amount, emoji) = when (habitType) {
                HabitType.SMOKING -> {
                    val preset = SmokingPreset.fromKey(presetKey)
                    preset?.amount to preset?.emoji
                }
                HabitType.ALCOHOL -> {
                    val preset = AlcoholPreset.fromKey(presetKey)
                    preset?.standardUnits?.roundToInt() to preset?.emoji
                }
                HabitType.SUGAR -> {
                    val preset = SugarPreset.fromKey(presetKey)
                    preset?.sugarGrams to preset?.emoji
                }
            }

            if (amount != null) {
                val entry = HabitEntry(
                    habitId = null,
                    habitType = habitType,
                    timestamp = LocalDateTime.now(),
                    amount = amount,
                    preset = presetKey,
                    note = emoji
                )

                habitEntryDao.insertEntry(entry)
                loadTodayData()

                // Проверить лимит
                val today = LocalDate.now().toString()
                val totalToday = habitEntryDao.getTotalForDay(habitType, today)
                val limit = when (habitType) {
                    HabitType.SMOKING -> WHOLimits.SMOKING_DAILY_LIMIT
                    HabitType.ALCOHOL -> WHOLimits.ALCOHOL_DAILY_LIMIT_MALE.toInt()
                    HabitType.SUGAR -> WHOLimits.SUGAR_DAILY_LIMIT
                }

                if (totalToday > limit) {
                    _uiState.value = _uiState.value.copy(
                        event = HabitUIEvent.LimitExceeded(habitType, totalToday, limit)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        event = HabitUIEvent.EntryAdded(habitType)
                    )
                }
            }
        }
    }

    /**
     * Удалить запись
     */
    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            // TODO: Add deleteEntry method to HabitEntryDao
            loadTodayData()
        }
    }

    /**
     * Очистить событие после его обработки
     */
    fun clearEvent() {
        _uiState.value = _uiState.value.copy(event = null)
    }

    /**
     * Расчет продолжительности жизни
     * Возвращает тройку: (до привычек, с текущим прогрессом, добавлено лет)
     */
    private fun calculateLifeExpectancy(
        profile: UserProfile?,
        habits: List<Habit>,
        smokingSummary: TodayHabitSummary,
        alcoholSummary: TodayHabitSummary,
        sugarSummary: TodayHabitSummary
    ): Triple<Double, Double, Double> {
        if (profile == null) {
            return Triple(75.0, 75.0, 0.0)
        }

        // Базовая продолжительность жизни
        val baseLifeExpectancy = when (profile.gender) {
            Gender.MALE -> 73.0
            Gender.FEMALE -> 79.0
            else -> 76.0
        }

        // Продолжительность "до" (предполагаем, что человек раньше имел все привычки на максимуме)
        val lifeExpectancyBefore = baseLifeExpectancy - 15.0 // -15 лет за все вредные привычки

        // Продолжительность "сейчас" с учетом текущего прогресса
        var lifeExpectancyCurrent = baseLifeExpectancy

        // Отнимаем годы за курение (если превышает норму)
        if (smokingSummary.hasExceededLimit) {
            val exceedPercentage = (smokingSummary.percentage - 100) / 100
            lifeExpectancyCurrent -= 5.0 * exceedPercentage.coerceIn(0f, 1f)
        } else {
            // Добавляем годы за соблюдение нормы
            lifeExpectancyCurrent += 2.0
        }

        // Отнимаем годы за алкоголь
        if (alcoholSummary.hasExceededLimit) {
            val exceedPercentage = (alcoholSummary.percentage - 100) / 100
            lifeExpectancyCurrent -= 4.0 * exceedPercentage.coerceIn(0f, 1f)
        } else {
            lifeExpectancyCurrent += 1.5
        }

        // Отнимаем годы за сахар
        if (sugarSummary.hasExceededLimit) {
            val exceedPercentage = (sugarSummary.percentage - 100) / 100
            lifeExpectancyCurrent -= 3.0 * exceedPercentage.coerceIn(0f, 1f)
        } else {
            lifeExpectancyCurrent += 1.0
        }

        val yearsGained = (lifeExpectancyCurrent - lifeExpectancyBefore).coerceAtLeast(0.0)

        return Triple(lifeExpectancyBefore, lifeExpectancyCurrent, yearsGained)
    }

    /**
     * Расчет дней, добавленных за текущий месяц
     */
    private suspend fun calculateDaysGainedThisMonth(): Int {
        // Упрощенный расчет: среднее соблюдение норм за месяц
        val today = LocalDate.now()

        // Подсчитываем дни в этом месяце, когда пользователь соблюдал нормы
        var daysWithinLimits = 0

        for (day in 1..today.dayOfMonth) {
            val date = today.withDayOfMonth(day).toString()

            val smokingTotal = habitEntryDao.getTotalForDay(HabitType.SMOKING, date)
            val alcoholTotal = habitEntryDao.getTotalForDay(HabitType.ALCOHOL, date)
            val sugarTotal = habitEntryDao.getTotalForDay(HabitType.SUGAR, date)

            val smokingOk = smokingTotal <= WHOLimits.SMOKING_DAILY_LIMIT
            val alcoholOk = alcoholTotal <= WHOLimits.ALCOHOL_DAILY_LIMIT_MALE.toInt()
            val sugarOk = sugarTotal <= WHOLimits.SUGAR_DAILY_LIMIT

            // Если хотя бы 2 из 3 привычек в норме - день засчитывается
            if ((smokingOk && alcoholOk) || (smokingOk && sugarOk) || (alcoholOk && sugarOk)) {
                daysWithinLimits++
            }
        }

        // Каждый день соблюдения норм добавляет ~0.5 дня к жизни
        return (daysWithinLimits * 0.5).roundToInt()
    }

    /**
     * Загрузить последние достижения (пока заглушка)
     */
    private fun loadRecentAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = 1,
                emoji = "🎯",
                title = "Первая неделя",
                description = "7 дней отслеживания привычек",
                category = AchievementCategory.TIME_BASED,
                requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SMOKING, 7),
                isUnlocked = true,
                unlockedAt = LocalDateTime.now().minusDays(2)
            ),
            Achievement(
                id = 2,
                emoji = "💪",
                title = "В пределах нормы",
                description = "3 дня подряд без превышений",
                category = AchievementCategory.HEALTH,
                requirement = AchievementRequirement.HealthScore(0.7f),
                isUnlocked = true,
                unlockedAt = LocalDateTime.now().minusDays(5)
            ),
            Achievement(
                id = 3,
                emoji = "🔥",
                title = "Стрик 5 дней",
                description = "Ежедневное отслеживание",
                category = AchievementCategory.TIME_BASED,
                requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SMOKING, 5),
                isUnlocked = true,
                unlockedAt = LocalDateTime.now().minusDays(1)
            )
        )
    }

    /**
     * Загрузить рекомендуемые статьи (пока заглушка)
     */
    private fun loadFeaturedArticles(): List<Article> {
        return listOf(
            Article(
                id = 1,
                title = "Влияние курения на продолжительность жизни",
                summary = "Исследование показало, что отказ от курения увеличивает продолжительность жизни в среднем на 10 лет",
                content = "Подробное содержание статьи",
                category = ArticleCategory.SMOKING,
                scientificLink = "https://www.who.int/news-room/fact-sheets/detail/tobacco",
                imageEmoji = "🚭",
                readTimeMinutes = 5,
                credibilityScore = 0.95
            ),
            Article(
                id = 2,
                title = "Сахар и здоровье сердца",
                summary = "Высокое потребление сахара связано с увеличением риска сердечно-сосудистых заболеваний на 38%",
                content = "Подробное содержание статьи",
                category = ArticleCategory.SUGAR,
                scientificLink = "https://www.who.int/news-room/fact-sheets/detail/healthy-diet",
                imageEmoji = "❤️",
                readTimeMinutes = 7,
                credibilityScore = 0.92
            ),
            Article(
                id = 3,
                title = "Алкоголь: безопасные дозы",
                summary = "ВОЗ рекомендует ограничить потребление алкоголя до 14 единиц в неделю для мужчин",
                content = "Подробное содержание статьи",
                category = ArticleCategory.ALCOHOL,
                scientificLink = "https://www.who.int/news-room/fact-sheets/detail/alcohol",
                imageEmoji = "🍷",
                readTimeMinutes = 4,
                credibilityScore = 0.94
            )
        )
    }
}