package com.quitsmoking.data.model

import java.time.LocalDateTime

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val category: AchievementCategory,
    val requirement: AchievementRequirement,
    val isUnlocked: Boolean = false,
    val unlockedAt: LocalDateTime? = null
)

enum class AchievementCategory(val displayName: String) {
    TIME_BASED("Временные"),
    HEALTH("Здоровье"),
    FINANCIAL("Экономия"),
    SOCIAL("Социальные")
}

sealed class AchievementRequirement {
    data class DaysWithoutHabit(val habitType: HabitType, val days: Int) : AchievementRequirement()
    data class LifeExtensionDays(val days: Int) : AchievementRequirement()
    data class HealthScore(val score: Float) : AchievementRequirement()
    data class MultipleHabitsQuit(val count: Int) : AchievementRequirement()
}

object AchievementsRepository {
    val achievements = listOf(
        // Временные достижения
        Achievement(
            id = 1,
            title = "Первый шаг",
            description = "Бросил курить на 1 день",
            emoji = "🌱",
            category = AchievementCategory.TIME_BASED,
            requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SMOKING, 1)
        ),
        Achievement(
            id = 2,
            title = "Неделя силы",
            description = "Без курения целую неделю",
            emoji = "💪",
            category = AchievementCategory.TIME_BASED,
            requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SMOKING, 7)
        ),
        Achievement(
            id = 3,
            title = "Месячный воин",
            description = "30 дней без сигарет",
            emoji = "🏆",
            category = AchievementCategory.TIME_BASED,
            requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SMOKING, 30)
        ),
        Achievement(
            id = 4,
            title = "Трезвая неделя",
            description = "7 дней без алкоголя",
            emoji = "🚫",
            category = AchievementCategory.TIME_BASED,
            requirement = AchievementRequirement.DaysWithoutHabit(HabitType.ALCOHOL, 7)
        ),
        Achievement(
            id = 5,
            title = "Сахарный детокс",
            description = "2 недели без избыточного сахара",
            emoji = "🍯",
            category = AchievementCategory.TIME_BASED,
            requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SUGAR, 14)
        ),
        
        // Достижения здоровья
        Achievement(
            id = 6,
            title = "Здоровое сердце",
            description = "Health Score достиг 70%",
            emoji = "❤️",
            category = AchievementCategory.HEALTH,
            requirement = AchievementRequirement.HealthScore(0.7f)
        ),
        Achievement(
            id = 7,
            title = "Железное здоровье",
            description = "Health Score достиг 90%",
            emoji = "🛡️",
            category = AchievementCategory.HEALTH,
            requirement = AchievementRequirement.HealthScore(0.9f)
        ),
        Achievement(
            id = 8,
            title = "Новая жизнь",
            description = "Добавлено 30 дней к жизни",
            emoji = "🌟",
            category = AchievementCategory.HEALTH,
            requirement = AchievementRequirement.LifeExtensionDays(30)
        ),
        
        // Финансовые достижения
        Achievement(
            id = 9,
            title = "Первая экономия",
            description = "Сэкономил деньги, отказавшись от вредных привычек",
            emoji = "💰",
            category = AchievementCategory.FINANCIAL,
            requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SMOKING, 1)
        ),
        Achievement(
            id = 10,
            title = "Тысяча рублей",
            description = "Сэкономил более 1000 рублей",
            emoji = "💸",
            category = AchievementCategory.FINANCIAL,
            requirement = AchievementRequirement.DaysWithoutHabit(HabitType.SMOKING, 5) // ~1000 рублей при цене 200₽/пачка
        ),
        
        // Социальные достижения
        Achievement(
            id = 11,
            title = "Мультизадачник",
            description = "Одновременно бросил 2 вредные привычки",
            emoji = "🎯",
            category = AchievementCategory.SOCIAL,
            requirement = AchievementRequirement.MultipleHabitsQuit(2)
        ),
        Achievement(
            id = 12,
            title = "Чемпион здоровья",
            description = "Отказался от всех вредных привычек",
            emoji = "👑",
            category = AchievementCategory.SOCIAL,
            requirement = AchievementRequirement.MultipleHabitsQuit(4)
        )
    )
    
    fun getAchievementsByCategory(category: AchievementCategory): List<Achievement> {
        return achievements.filter { it.category == category }
    }
    
    fun getAllCategories(): List<AchievementCategory> {
        return AchievementCategory.values().toList()
    }
    
    fun checkAchievements(
        habits: List<Habit>,
        healthScore: Float,
        lifeExtensionDays: Int
    ): List<Achievement> {
        val unlockedAchievements = mutableListOf<Achievement>()
        
        achievements.forEach { achievement ->
            val isUnlocked = when (achievement.requirement) {
                is AchievementRequirement.DaysWithoutHabit -> {
                    val habit = habits.find { it.type == achievement.requirement.habitType }
                    habit != null && !habit.isActive && habit.quitDate != null &&
                    java.time.temporal.ChronoUnit.DAYS.between(
                        habit.quitDate.toLocalDate(),
                        LocalDateTime.now().toLocalDate()
                    ) >= achievement.requirement.days
                }
                is AchievementRequirement.HealthScore -> {
                    healthScore >= achievement.requirement.score
                }
                is AchievementRequirement.LifeExtensionDays -> {
                    lifeExtensionDays >= achievement.requirement.days
                }
                is AchievementRequirement.MultipleHabitsQuit -> {
                    habits.count { !it.isActive } >= achievement.requirement.count
                }
            }
            
            if (isUnlocked) {
                unlockedAchievements.add(achievement.copy(
                    isUnlocked = true,
                    unlockedAt = LocalDateTime.now()
                ))
            } else {
                unlockedAchievements.add(achievement)
            }
        }
        
        return unlockedAchievements
    }
}