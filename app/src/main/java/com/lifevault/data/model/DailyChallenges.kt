package com.lifevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "daily_challenges")
data class DailyChallenge(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: ChallengeCategory,
    val difficulty: ChallengeDifficulty,
    val targetHabits: List<HabitType> = emptyList(), // Связанные привычки
    val rewardDays: Int,                             // Награда в днях жизни
    val rewardTimeBank: Long,                        // Награда в Time Bank
    val duration: ChallengeDuration,                 // Длительность вызова
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "user_challenge_progress")
data class UserChallengeProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val challengeId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ChallengeStatus,
    val currentStreak: Int = 0,                      // Текущая серия
    val maxStreak: Int = 0,                          // Максимальная серия
    val completedDays: Int = 0,                      // Выполненных дней
    val totalDays: Int,                              // Общее количество дней
    val progress: Double = 0.0,                      // Прогресс 0-1
    val lastActivityDate: LocalDate? = null,
    val rewardsEarned: Long = 0,                     // Заработанные награды
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ChallengeCategory(val displayName: String, val icon: String, val color: String) {
    QUIT_HABITS("Отказ от привычек", "🚫", "#E74C3C"),
    HEALTHY_LIFESTYLE("Здоровый образ жизни", "💪", "#2ECC71"),
    MINDFULNESS("Осознанность", "🧘", "#9B59B6"),
    PHYSICAL_ACTIVITY("Физическая активность", "🏃", "#3498DB"),
    NUTRITION("Питание", "🥗", "#F39C12"),
    SLEEP_QUALITY("Качество сна", "😴", "#34495E"),
    STRESS_MANAGEMENT("Управление стрессом", "🌅", "#1ABC9C")
}

enum class ChallengeDifficulty(val displayName: String, val multiplier: Double, val emoji: String) {
    BEGINNER("Новичок", 1.0, "🌱"),
    INTERMEDIATE("Средний", 1.5, "🌿"),
    ADVANCED("Продвинутый", 2.0, "🌳"),
    EXPERT("Эксперт", 3.0, "🏆")
}

enum class ChallengeDuration(val days: Int, val displayName: String) {
    ONE_DAY(1, "1 день"),
    THREE_DAYS(3, "3 дня"),
    ONE_WEEK(7, "1 неделя"),
    TWO_WEEKS(14, "2 недели"),
    ONE_MONTH(30, "1 месяц"),
    THREE_MONTHS(90, "3 месяца"),
    SIX_MONTHS(180, "6 месяцев"),
    ONE_YEAR(365, "1 год")
}

enum class ChallengeStatus(val displayName: String, val color: String) {
    NOT_STARTED("Не начат", "#95A5A6"),
    IN_PROGRESS("В процессе", "#3498DB"),
    COMPLETED("Завершен", "#2ECC71"),
    FAILED("Провален", "#E74C3C"),
    PAUSED("Приостановлен", "#F39C12")
}

// Предопределенные вызовы
object DefaultChallenges {
    
    val QUIT_SMOKING_CHALLENGES = listOf(
        DailyChallenge(
            title = "День без сигарет",
            description = "Проведите целый день без единой сигареты",
            category = ChallengeCategory.QUIT_HABITS,
            difficulty = ChallengeDifficulty.BEGINNER,
            targetHabits = listOf(HabitType.SMOKING),
            rewardDays = 1,
            rewardTimeBank = 24,
            duration = ChallengeDuration.ONE_DAY
        ),
        DailyChallenge(
            title = "Неделя без курения",
            description = "7 дней подряд без сигарет - первый серьезный шаг к здоровью",
            category = ChallengeCategory.QUIT_HABITS,
            difficulty = ChallengeDifficulty.INTERMEDIATE,
            targetHabits = listOf(HabitType.SMOKING),
            rewardDays = 7,
            rewardTimeBank = 168,
            duration = ChallengeDuration.ONE_WEEK
        ),
        DailyChallenge(
            title = "Месяц без курения",
            description = "30 дней - критическая веха в борьбе с никотиновой зависимостью",
            category = ChallengeCategory.QUIT_HABITS,
            difficulty = ChallengeDifficulty.ADVANCED,
            targetHabits = listOf(HabitType.SMOKING),
            rewardDays = 30,
            rewardTimeBank = 720,
            duration = ChallengeDuration.ONE_MONTH
        )
    )
    
    val ALCOHOL_CHALLENGES = listOf(
        DailyChallenge(
            title = "Трезвый день",
            description = "Один день без алкоголя - покажите силу воли",
            category = ChallengeCategory.QUIT_HABITS,
            difficulty = ChallengeDifficulty.BEGINNER,
            targetHabits = listOf(HabitType.ALCOHOL),
            rewardDays = 1,
            rewardTimeBank = 12,
            duration = ChallengeDuration.ONE_DAY
        ),
        DailyChallenge(
            title = "Сухая неделя",
            description = "7 дней без алкоголя для перезагрузки организма",
            category = ChallengeCategory.QUIT_HABITS,
            difficulty = ChallengeDifficulty.INTERMEDIATE,
            targetHabits = listOf(HabitType.ALCOHOL),
            rewardDays = 3,
            rewardTimeBank = 84,
            duration = ChallengeDuration.ONE_WEEK
        ),
        DailyChallenge(
            title = "Трезвый январь",
            description = "Месяц без алкоголя - популярный мировой флешмоб",
            category = ChallengeCategory.QUIT_HABITS,
            difficulty = ChallengeDifficulty.ADVANCED,
            targetHabits = listOf(HabitType.ALCOHOL),
            rewardDays = 15,
            rewardTimeBank = 360,
            duration = ChallengeDuration.ONE_MONTH
        )
    )
    
    val SUGAR_CHALLENGES = listOf(
        DailyChallenge(
            title = "День без сладкого",
            description = "Избегайте добавленного сахара целый день",
            category = ChallengeCategory.NUTRITION,
            difficulty = ChallengeDifficulty.BEGINNER,
            targetHabits = listOf(HabitType.SUGAR),
            rewardDays = 0,
            rewardTimeBank = 6,
            duration = ChallengeDuration.ONE_DAY
        ),
        DailyChallenge(
            title = "Неделя без сахара",
            description = "7 дней питания без добавленного сахара",
            category = ChallengeCategory.NUTRITION,
            difficulty = ChallengeDifficulty.INTERMEDIATE,
            targetHabits = listOf(HabitType.SUGAR),
            rewardDays = 2,
            rewardTimeBank = 42,
            duration = ChallengeDuration.ONE_WEEK
        ),
        DailyChallenge(
            title = "Sugar Detox - 30 дней",
            description = "Полная детоксикация от сахара на месяц",
            category = ChallengeCategory.NUTRITION,
            difficulty = ChallengeDifficulty.EXPERT,
            targetHabits = listOf(HabitType.SUGAR),
            rewardDays = 10,
            rewardTimeBank = 180,
            duration = ChallengeDuration.ONE_MONTH
        )
    )
    
    val LIFESTYLE_CHALLENGES = listOf(
        DailyChallenge(
            title = "10,000 шагов",
            description = "Пройдите 10,000 шагов за день",
            category = ChallengeCategory.PHYSICAL_ACTIVITY,
            difficulty = ChallengeDifficulty.BEGINNER,
            rewardDays = 0,
            rewardTimeBank = 3,
            duration = ChallengeDuration.ONE_DAY
        ),
        DailyChallenge(
            title = "8 часов сна",
            description = "Спите полноценные 8 часов каждую ночь",
            category = ChallengeCategory.SLEEP_QUALITY,
            difficulty = ChallengeDifficulty.INTERMEDIATE,
            rewardDays = 1,
            rewardTimeBank = 12,
            duration = ChallengeDuration.ONE_WEEK
        ),
        DailyChallenge(
            title = "Медитация каждый день",
            description = "10 минут медитации ежедневно в течение месяца",
            category = ChallengeCategory.MINDFULNESS,
            difficulty = ChallengeDifficulty.ADVANCED,
            rewardDays = 5,
            rewardTimeBank = 120,
            duration = ChallengeDuration.ONE_MONTH
        ),
        DailyChallenge(
            title = "2 литра воды в день",
            description = "Выпивайте не менее 2 литров чистой воды ежедневно",
            category = ChallengeCategory.NUTRITION,
            difficulty = ChallengeDifficulty.BEGINNER,
            rewardDays = 0,
            rewardTimeBank = 2,
            duration = ChallengeDuration.ONE_WEEK
        )
    )
    
    val ALL_CHALLENGES = QUIT_SMOKING_CHALLENGES + ALCOHOL_CHALLENGES + 
                         SUGAR_CHALLENGES + LIFESTYLE_CHALLENGES
}

// Система прогресса и наград
object ChallengeSystem {
    
    fun calculateReward(
        challenge: DailyChallenge,
        completionRate: Double, // 0.0 - 1.0
        streakBonus: Int = 0
    ): ChallengeReward {
        val baseReward = (challenge.rewardTimeBank * completionRate).toLong()
        val difficultyMultiplier = challenge.difficulty.multiplier
        val streakMultiplier = 1.0 + (streakBonus * 0.1) // 10% за каждый день серии
        
        val totalReward = (baseReward * difficultyMultiplier * streakMultiplier).toLong()
        val lifeDays = (challenge.rewardDays * completionRate * difficultyMultiplier).toInt()
        
        return ChallengeReward(
            timeBankReward = totalReward,
            lifeDaysReward = lifeDays,
            streakBonus = streakBonus,
            completionRate = completionRate
        )
    }
    
    fun getPersonalizedChallenges(
        userHabits: List<Habit>,
        completedChallenges: List<UserChallengeProgress>,
        difficulty: ChallengeDifficulty = ChallengeDifficulty.BEGINNER
    ): List<DailyChallenge> {
        val activeHabits = userHabits.filter { it.isActive }.map { it.type }
        val completedChallengeIds = completedChallenges
            .filter { it.status == ChallengeStatus.COMPLETED }
            .map { it.challengeId }
        
        return DefaultChallenges.ALL_CHALLENGES.filter { challenge ->
            // Показываем вызовы для активных привычек пользователя
            challenge.targetHabits.isEmpty() || 
            challenge.targetHabits.any { it in activeHabits } &&
            // Исключаем уже завершенные
            challenge.id !in completedChallengeIds &&
            // Фильтруем по сложности
            challenge.difficulty.ordinal <= difficulty.ordinal
        }.sortedBy { it.difficulty.ordinal }
    }
    
    fun getDailyChallengeOfTheDay(date: LocalDate = LocalDate.now()): DailyChallenge {
        // Используем дату как сид для стабильного "случайного" выбора
        val daysSinceEpoch = date.toEpochDay()
        val challengeIndex = (daysSinceEpoch % DefaultChallenges.ALL_CHALLENGES.size).toInt()
        return DefaultChallenges.ALL_CHALLENGES[challengeIndex]
    }
}

data class ChallengeReward(
    val timeBankReward: Long,
    val lifeDaysReward: Int,
    val streakBonus: Int,
    val completionRate: Double
)

// Статистика вызовов
data class ChallengeStats(
    val totalChallengesStarted: Int,
    val totalChallengesCompleted: Int,
    val completionRate: Double,
    val longestStreak: Int,
    val currentStreak: Int,
    val totalRewardsEarned: Long,
    val favoriteCategory: ChallengeCategory?,
    val averageDifficulty: ChallengeDifficulty
)

// Система мотивационных уведомлений
object ChallengeMotivation {
    
    private val motivationalMessages = mapOf(
        ChallengeStatus.IN_PROGRESS to listOf(
            "Вы на правильном пути! Продолжайте в том же духе! 💪",
            "Каждый день делает вас сильнее! 🌟",
            "Помните, зачем вы начали этот путь! 🎯",
            "Ваше здоровье стоит этих усилий! ❤️"
        ),
        ChallengeStatus.COMPLETED to listOf(
            "Поздравляем! Вы справились с вызовом! 🎉",
            "Отличная работа! Вы добавили дни к своей жизни! ⏰",
            "Вы доказали себе, что можете все! 🏆",
            "Это победа, которой можно гордиться! 🌟"
        ),
        ChallengeStatus.FAILED to listOf(
            "Не сдавайтесь! Каждая попытка - это опыт! 🌱",
            "Неудача - это возможность начать снова, но умнее! 💡",
            "Великие достижения требуют времени! ⏳",
            "Попробуйте более легкий вызов для начала! 🎯"
        )
    )
    
    fun getMotivationalMessage(status: ChallengeStatus): String {
        val messages = motivationalMessages[status] ?: return "Продолжайте развиваться!"
        return messages.random()
    }
    
    fun getStreakMessage(streak: Int): String {
        return when {
            streak >= 30 -> "Невероятно! ${streak} дней подряд! Вы легенда! 👑"
            streak >= 14 -> "Две недели подряд! Отличная серия! 🔥"
            streak >= 7 -> "Неделя подряд! Вы в огне! 🌟"
            streak >= 3 -> "Третий день подряд! Набираете обороты! 💪"
            streak == 2 -> "Второй день подряд! Хорошее начало! 🌱"
            else -> "Каждый день - это новая возможность! ✨"
        }
    }
}