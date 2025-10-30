package com.lifevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "time_bank")
data class TimeBank(
    @PrimaryKey
    val userId: Long = 1,
    val totalDaysEarned: Long = 0,        // Общее количество заработанных дней
    val totalDaysSpent: Long = 0,         // Потраченные дни на покупки
    val currentBalance: Long = 0,         // Текущий баланс дней
    val lifetimeEarnings: Long = 0,       // Общие заработанные дни за все время
    val streakDays: Int = 0,              // Текущая серия дней без нарушений
    val longestStreak: Int = 0,           // Самая длинная серия
    val multiplierActive: Boolean = false, // Активный множитель
    val multiplierValue: Double = 1.0,     // Значение множителя
    val multiplierExpiresAt: LocalDateTime? = null, // Когда истекает множитель
    val lastActivityDate: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "time_bank_transactions")
data class TimeBankTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val type: TransactionType,
    val amount: Long,                     // Количество дней (положительное для заработка)
    val source: String,                   // Источник транзакции
    val description: String,              // Описание транзакции
    val habitType: HabitType? = null,     // Связанная привычка
    val achievementId: Long? = null,      // Связанное достижение
    val balanceBefore: Long,              // Баланс до транзакции
    val balanceAfter: Long,               // Баланс после транзакции
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType(val displayName: String, val icon: String) {
    EARNED_QUIT_HABIT("Заработано за отказ от привычки", "🎯"),
    EARNED_DAILY_GOAL("Заработано за ежедневную цель", "✅"),
    EARNED_WEEKLY_CHALLENGE("Заработано за недельный вызов", "🏆"),
    EARNED_ACHIEVEMENT("Заработано за достижение", "🏅"),
    EARNED_STREAK_BONUS("Бонус за серию", "🔥"),
    EARNED_LIFESTYLE_IMPROVEMENT("Улучшение образа жизни", "💪"),
    SPENT_REWARD("Потрачено на награду", "🎁"),
    SPENT_PREMIUM_FEATURE("Потрачено на премиум функцию", "⭐"),
    ADJUSTMENT_ADMIN("Административная корректировка", "⚙️")
}

// Магазин временных наград
@Entity(tableName = "time_bank_rewards")
data class TimeBankReward(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val cost: Long,                       // Стоимость в днях жизни
    val category: RewardCategory,
    val icon: String,
    val isActive: Boolean = true,
    val isPremium: Boolean = false,
    val unlockRequirement: String? = null, // Требование для разблокировки
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class RewardCategory(val displayName: String, val emoji: String) {
    VIRTUAL_REWARDS("Виртуальные награды", "🏆"),
    APP_FEATURES("Функции приложения", "⚙️"),
    HEALTH_INSIGHTS("Здоровье и аналитика", "📊"),
    CUSTOMIZATION("Персонализация", "🎨"),
    ACHIEVEMENTS("Достижения", "🏅")
}

// Предопределенные награды в магазине
object TimeBankRewards {
    val DEFAULT_REWARDS = listOf(
        TimeBankReward(
            title = "Золотая медаль",
            description = "Виртуальная золотая медаль за достижения",
            cost = 30,
            category = RewardCategory.VIRTUAL_REWARDS,
            icon = "🥇"
        ),
        TimeBankReward(
            title = "Детальная статистика",
            description = "Разблокировать расширенную статистику на месяц",
            cost = 90,
            category = RewardCategory.APP_FEATURES,
            icon = "📈"
        ),
        TimeBankReward(
            title = "Персональный тренер ИИ",
            description = "ИИ-советы для улучшения здоровья на неделю",
            cost = 180,
            category = RewardCategory.HEALTH_INSIGHTS,
            icon = "🤖"
        ),
        TimeBankReward(
            title = "Темная тема",
            description = "Эксклюзивная темная тема приложения",
            cost = 60,
            category = RewardCategory.CUSTOMIZATION,
            icon = "🌙"
        ),
        TimeBankReward(
            title = "Значок 'Легенда здоровья'",
            description = "Эксклюзивный значок в профиле",
            cost = 365,
            category = RewardCategory.ACHIEVEMENTS,
            icon = "👑",
            isPremium = true
        ),
        TimeBankReward(
            title = "Экспорт данных",
            description = "Экспорт всех данных о здоровье в CSV",
            cost = 45,
            category = RewardCategory.APP_FEATURES,
            icon = "📤"
        ),
        TimeBankReward(
            title = "Предсказание продолжительности жизни",
            description = "Персонализированный прогноз с ИИ на месяц",
            cost = 120,
            category = RewardCategory.HEALTH_INSIGHTS,
            icon = "🔮"
        ),
        TimeBankReward(
            title = "Анимированный аватар",
            description = "Анимированный аватар в зависимости от прогресса",
            cost = 75,
            category = RewardCategory.CUSTOMIZATION,
            icon = "🎭"
        )
    )
}

// Система заработка времени
object TimeEarningSystem {
    
    // Базовые начисления за отказ от привычек (в днях)
    val HABIT_QUIT_REWARDS = mapOf(
        HabitType.SMOKING to 5,    // 5 дней за отказ от курения
        HabitType.ALCOHOL to 3,    // 3 дня за отказ от алкоголя
        HabitType.SUGAR to 2       // 2 дня за отказ от сахара
    )
    
    // Ежедневные начисления за поддержание отказа
    val DAILY_MAINTENANCE_REWARDS = mapOf(
        HabitType.SMOKING to 0.2,  // 0.2 дня (4.8 часа) за каждый день без курения
        HabitType.ALCOHOL to 0.1,  // 0.1 дня (2.4 часа) за каждый день без алкоголя
        HabitType.SUGAR to 0.05    // 0.05 дня (1.2 часа) за каждый день без сахара
    )
    
    // Бонусы за серии
    val STREAK_BONUSES = mapOf(
        7 to 3,      // 3 дня за неделю
        14 to 7,     // 7 дней за 2 недели
        30 to 15,    // 15 дней за месяц
        90 to 50,    // 50 дней за 3 месяца
        180 to 120,  // 120 дней за полгода
        365 to 365   // 365 дней (1 год) за год
    )
    
    // Множители за долгосрочные достижения
    fun getStreakMultiplier(streakDays: Int): Double {
        return when {
            streakDays >= 365 -> 3.0  // 3x множитель после года
            streakDays >= 180 -> 2.5  // 2.5x множитель после полугода
            streakDays >= 90 -> 2.0   // 2x множитель после 3 месяцев
            streakDays >= 30 -> 1.5   // 1.5x множитель после месяца
            streakDays >= 7 -> 1.2    // 1.2x множитель после недели
            else -> 1.0
        }
    }
    
    // Расчет дневного заработка
    fun calculateDailyEarning(
        quitHabits: List<Habit>,
        streakDays: Int,
        lifestyleScore: Float
    ): Double {
        var totalEarning = 0.0
        
        // Начисления за поддержание отказа от привычек
        quitHabits.filter { !it.isActive }.forEach { habit ->
            val baseReward = DAILY_MAINTENANCE_REWARDS[habit.type] ?: 0.0
            totalEarning += baseReward
        }
        
        // Бонус за здоровый образ жизни
        totalEarning += lifestyleScore * 0.1 // До 0.1 дня за отличный образ жизни
        
        // Применяем множитель за серию
        val multiplier = getStreakMultiplier(streakDays)
        totalEarning *= multiplier
        
        return totalEarning
    }
}

// Система покупки наград
data class RewardPurchase(
    val rewardId: Long,
    val userId: Long,
    val purchaseDate: LocalDateTime,
    val cost: Long,
    val isActive: Boolean = true,
    val expiresAt: LocalDateTime? = null // Для временных наград
)

// Статистика Time Bank
data class TimeBankStats(
    val totalDaysEarned: Long,
    val averageDailyEarning: Double,
    val biggestSingleEarning: Long,
    val totalSpent: Long,
    val currentBalance: Long,
    val streakDays: Int,
    val rewardsPurchased: Int,
    val favoriteRewardCategory: RewardCategory?
)