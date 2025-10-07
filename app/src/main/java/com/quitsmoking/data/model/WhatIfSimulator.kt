package com.quitsmoking.data.model

import java.time.LocalDate

// Данные для What-If симулятора
data class WhatIfSimulation(
    val userId: Long,
    val baseScenario: LifeScenario,        // Текущий сценарий пользователя
    val whatIfScenario: LifeScenario,      // Гипотетический сценарий
    val comparison: LifeComparison,         // Сравнение результатов
    val confidence: Double = 0.85           // Уверенность в прогнозе (0-1)
)

data class LifeScenario(
    val profile: UserProfile,
    val habits: Map<HabitType, HabitScenario>,
    val lifestyleFactors: Map<String, Double>, // Факторы образа жизни (0-1)
    val calculatedLifeExpectancy: Double,
    val confidenceInterval: Pair<Double, Double>
)

data class HabitScenario(
    val habitType: HabitType,
    val status: HabitStatus,
    val intensity: Double = 1.0,         // 0.0 = нет привычки, 1.0 = полная интенсивность
    val dailyAmount: Int = 0,            // Количество в день
    val yearsWithHabit: Int = 0,         // Сколько лет уже с привычкой
    val quitPlanned: Boolean = false      // Планируется ли бросить
)

enum class HabitStatus(val displayName: String) {
    NEVER_HAD("Никогда не имел"),
    CURRENT_USER("Текущий пользователь"),
    FORMER_USER("Бывший пользователь"),
    PLANNED_TO_QUIT("Планирует бросить"),
    REDUCING("Снижает потребление")
}

data class LifeComparison(
    val lifeExpectancyDifference: Double,  // Разница в годах
    val daysDifference: Long,              // Разница в днях
    val percentageChange: Double,          // Процентное изменение
    val healthScoreDifference: Double,     // Разница в показателе здоровья
    val topImprovements: List<ImpactFactor>, // Топ факторов улучшения
    val topRisks: List<ImpactFactor>       // Топ факторов риска
)

data class ImpactFactor(
    val factor: String,                    // Название фактора
    val habitType: HabitType?,            // Связанная привычка
    val impact: Double,                   // Влияние в днях
    val category: ImpactCategory,
    val description: String,              // Объяснение
    val confidence: Double                // Уверенность в оценке
)

enum class ImpactCategory(val displayName: String, val color: String) {
    HABIT_CHANGE("Изменение привычки", "#FF6B6B"),
    LIFESTYLE("Образ жизни", "#4ECDC4"),
    MEDICAL("Медицинские факторы", "#45B7D1"),
    ENVIRONMENTAL("Окружающая среда", "#96CEB4"),
    GENETIC("Генетические факторы", "#FECA57")
}

// Фабрика сценариев
object ScenarioFactory {
    
    fun createCurrentScenario(
        profile: UserProfile,
        habits: List<Habit>
    ): LifeScenario {
        val habitScenarios = habits.associate { habit ->
            habit.type to HabitScenario(
                habitType = habit.type,
                status = if (habit.isActive) HabitStatus.CURRENT_USER else HabitStatus.FORMER_USER,
                intensity = habit.intensity.multiplier,
                dailyAmount = habit.dailyUsage ?: 0,
                yearsWithHabit = habit.startedDate?.let { 
                    java.time.Period.between(it.toLocalDate(), LocalDate.now()).years 
                } ?: 0,
                quitPlanned = false
            )
        }
        
        // Базовые факторы образа жизни (можно расширить)
        val lifestyleFactors = mapOf(
            "physical_activity" to 0.6,    // Средний уровень активности
            "sleep_quality" to 0.7,        // Хорошее качество сна
            "stress_management" to 0.5,    // Средний уровень стресса
            "social_connections" to 0.8,   // Хорошие социальные связи
            "diet_quality" to 0.6,         // Средняя диета
            "preventive_care" to 0.7       // Регулярные медосмотры
        )
        
        val calculation = LifeCalculator.calculateLifeExpectancy(profile, habits, 0.6f)
        
        return LifeScenario(
            profile = profile,
            habits = habitScenarios,
            lifestyleFactors = lifestyleFactors,
            calculatedLifeExpectancy = calculation.finalLifeExpectancy,
            confidenceInterval = calculation.confidenceInterval
        )
    }
    
    fun createWhatIfScenario(
        baseScenario: LifeScenario,
        changes: Map<String, Any>
    ): LifeScenario {
        val modifiedHabits = baseScenario.habits.toMutableMap()
        val modifiedLifestyle = baseScenario.lifestyleFactors.toMutableMap()
        
        // Применяем изменения
        changes.forEach { (key, value) ->
            when {
                key.startsWith("habit_") -> {
                    val habitType = HabitType.valueOf(key.removePrefix("habit_").uppercase())
                    when (value) {
                        is HabitScenario -> modifiedHabits[habitType] = value
                        is Double -> {
                            val existingHabit = modifiedHabits[habitType] ?: HabitScenario(habitType, HabitStatus.NEVER_HAD)
                            modifiedHabits[habitType] = existingHabit.copy(intensity = value)
                        }
                    }
                }
                key.startsWith("lifestyle_") -> {
                    val factor = key.removePrefix("lifestyle_")
                    if (value is Double) {
                        modifiedLifestyle[factor] = value.coerceIn(0.0, 1.0)
                    }
                }
            }
        }
        
        // Пересчитываем ожидаемую продолжительность жизни
        val simulatedHabits = convertToHabitList(modifiedHabits)
        val avgLifestyleScore = modifiedLifestyle.values.average().toFloat()
        val calculation = LifeCalculator.calculateLifeExpectancy(
            baseScenario.profile, 
            simulatedHabits, 
            avgLifestyleScore
        )
        
        return baseScenario.copy(
            habits = modifiedHabits,
            lifestyleFactors = modifiedLifestyle,
            calculatedLifeExpectancy = calculation.finalLifeExpectancy,
            confidenceInterval = calculation.confidenceInterval
        )
    }
    
    private fun convertToHabitList(habitScenarios: Map<HabitType, HabitScenario>): List<Habit> {
        return habitScenarios.map { (type, scenario) ->
            Habit(
                type = type,
                isActive = scenario.status == HabitStatus.CURRENT_USER,
                dailyUsage = scenario.dailyAmount.takeIf { it > 0 },
                intensity = HabitIntensity.values().find { 
                    kotlin.math.abs(it.multiplier - scenario.intensity) < 0.1 
                } ?: HabitIntensity.MODERATE
            )
        }
    }
}

// Предустановленные сценарии для быстрого тестирования
object PresetScenarios {
    
    fun getOptimalHealthScenario(baseScenario: LifeScenario): Map<String, Any> {
        return mapOf(
            // Убираем все вредные привычки
            "habit_smoking" to HabitScenario(HabitType.SMOKING, HabitStatus.NEVER_HAD, 0.0),
            "habit_alcohol" to HabitScenario(HabitType.ALCOHOL, HabitStatus.FORMER_USER, 0.1), // Минимальное потребление
            "habit_sugar" to HabitScenario(HabitType.SUGAR, HabitStatus.FORMER_USER, 0.3),
            
            // Максимизируем здоровые факторы
            "lifestyle_physical_activity" to 1.0,
            "lifestyle_sleep_quality" to 1.0,
            "lifestyle_stress_management" to 0.9,
            "lifestyle_social_connections" to 1.0,
            "lifestyle_diet_quality" to 0.95,
            "lifestyle_preventive_care" to 1.0
        )
    }
    
    fun getWorstCaseScenario(baseScenario: LifeScenario): Map<String, Any> {
        return mapOf(
            // Максимизируем вредные привычки
            "habit_smoking" to HabitScenario(HabitType.SMOKING, HabitStatus.CURRENT_USER, 1.5, 40),
            "habit_alcohol" to HabitScenario(HabitType.ALCOHOL, HabitStatus.CURRENT_USER, 2.0, 50),
            "habit_sugar" to HabitScenario(HabitType.SUGAR, HabitStatus.CURRENT_USER, 1.8, 150),
            
            // Минимизируем здоровые факторы
            "lifestyle_physical_activity" to 0.1,
            "lifestyle_sleep_quality" to 0.3,
            "lifestyle_stress_management" to 0.2,
            "lifestyle_social_connections" to 0.3,
            "lifestyle_diet_quality" to 0.2,
            "lifestyle_preventive_care" to 0.1
        )
    }
    
    fun getRealisticImprovementScenario(baseScenario: LifeScenario): Map<String, Any> {
        val changes = mutableMapOf<String, Any>()
        
        // Реалистичные улучшения существующих привычек
        baseScenario.habits.forEach { (habitType, scenario) ->
            when (scenario.status) {
                HabitStatus.CURRENT_USER -> {
                    // Снижаем интенсивность на 50%
                    changes["habit_${habitType.name.lowercase()}"] = scenario.copy(
                        status = HabitStatus.REDUCING,
                        intensity = scenario.intensity * 0.5,
                        dailyAmount = (scenario.dailyAmount * 0.5).toInt()
                    )
                }
                else -> {
                    // Оставляем как есть
                    changes["habit_${habitType.name.lowercase()}"] = scenario
                }
            }
        }
        
        // Умеренные улучшения образа жизни
        baseScenario.lifestyleFactors.forEach { (factor, value) ->
            val improvement = (value + 0.2).coerceAtMost(1.0)
            changes["lifestyle_$factor"] = improvement
        }
        
        return changes
    }
}

// Анализатор сценариев
object ScenarioAnalyzer {
    
    fun compareScenarios(base: LifeScenario, whatIf: LifeScenario): LifeComparison {
        val lifeExpectancyDiff = whatIf.calculatedLifeExpectancy - base.calculatedLifeExpectancy
        val daysDiff = (lifeExpectancyDiff * 365.25).toLong()
        val percentageChange = (lifeExpectancyDiff / base.calculatedLifeExpectancy) * 100
        
        val improvements = mutableListOf<ImpactFactor>()
        val risks = mutableListOf<ImpactFactor>()
        
        // Анализируем изменения в привычках
        base.habits.forEach { (habitType, baseHabit) ->
            val whatIfHabit = whatIf.habits[habitType]
            if (whatIfHabit != null && baseHabit.intensity != whatIfHabit.intensity) {
                val coefficient = LifeCalculator.getCoefficient(habitType)
                if (coefficient != null) {
                    val intensityChange = baseHabit.intensity - whatIfHabit.intensity
                    val impactDays = intensityChange * coefficient.positiveImpactOnQuit * 0.5
                    
                    val factor = ImpactFactor(
                        factor = "${habitType.displayName} изменение",
                        habitType = habitType,
                        impact = impactDays,
                        category = ImpactCategory.HABIT_CHANGE,
                        description = when {
                            intensityChange > 0.5 -> "Значительное снижение ${habitType.displayName.lowercase()}"
                            intensityChange > 0.1 -> "Умеренное снижение ${habitType.displayName.lowercase()}"
                            intensityChange < -0.5 -> "Значительное увеличение ${habitType.displayName.lowercase()}"
                            intensityChange < -0.1 -> "Умеренное увеличение ${habitType.displayName.lowercase()}"
                            else -> "Незначительное изменение ${habitType.displayName.lowercase()}"
                        },
                        confidence = 0.8
                    )
                    
                    if (impactDays > 0) improvements.add(factor) else risks.add(factor)
                }
            }
        }
        
        // Анализируем изменения образа жизни
        base.lifestyleFactors.forEach { (factor, baseValue) ->
            val whatIfValue = whatIf.lifestyleFactors[factor] ?: baseValue
            if (kotlin.math.abs(baseValue - whatIfValue) > 0.1) {
                val change = whatIfValue - baseValue
                val impactDays = change * (LifeCalculator.getLifestyleFactor(factor) ?: 0.0)
                
                val impactFactor = ImpactFactor(
                    factor = getLifestyleFactorName(factor),
                    habitType = null,
                    impact = impactDays,
                    category = ImpactCategory.LIFESTYLE,
                    description = when {
                        change > 0.3 -> "Значительное улучшение в области ${getLifestyleFactorName(factor).lowercase()}"
                        change > 0.1 -> "Умеренное улучшение в области ${getLifestyleFactorName(factor).lowercase()}"
                        change < -0.3 -> "Значительное ухудшение в области ${getLifestyleFactorName(factor).lowercase()}"
                        change < -0.1 -> "Умеренное ухудшение в области ${getLifestyleFactorName(factor).lowercase()}"
                        else -> "Незначительное изменение в области ${getLifestyleFactorName(factor).lowercase()}"
                    },
                    confidence = 0.7
                )
                
                if (impactDays > 0) improvements.add(impactFactor) else risks.add(impactFactor)
            }
        }
        
        return LifeComparison(
            lifeExpectancyDifference = lifeExpectancyDiff,
            daysDifference = daysDiff,
            percentageChange = percentageChange,
            healthScoreDifference = 0.0, // Можно добавить расчет
            topImprovements = improvements.sortedByDescending { it.impact }.take(5),
            topRisks = risks.sortedBy { it.impact }.take(5)
        )
    }
    
    private fun getLifestyleFactorName(factor: String): String {
        return when (factor) {
            "physical_activity" -> "Физическая активность"
            "sleep_quality" -> "Качество сна"
            "stress_management" -> "Управление стрессом"
            "social_connections" -> "Социальные связи"
            "diet_quality" -> "Качество питания"
            "preventive_care" -> "Профилактическая медицина"
            else -> factor.replace("_", " ").capitalize()
        }
    }
}

// Настройки слайдеров для UI
data class SliderConfig(
    val key: String,
    val title: String,
    val description: String,
    val minValue: Double,
    val maxValue: Double,
    val currentValue: Double,
    val step: Double,
    val unit: String,
    val category: SliderCategory,
    val icon: String
)

enum class SliderCategory(val displayName: String, val color: String) {
    HABITS("Привычки", "#FF6B6B"),
    LIFESTYLE("Образ жизни", "#4ECDC4"),
    MEDICAL("Медицинские", "#45B7D1")
}

object SliderConfigFactory {
    
    fun createHabitSliders(currentHabits: Map<HabitType, HabitScenario>): List<SliderConfig> {
        return HabitType.values().map { habitType ->
            val current = currentHabits[habitType]
            SliderConfig(
                key = "habit_${habitType.name.lowercase()}_intensity",
                title = habitType.displayName,
                description = "Интенсивность потребления ${habitType.displayName.lowercase()}",
                minValue = 0.0,
                maxValue = 2.0,
                currentValue = current?.intensity ?: 0.0,
                step = 0.1,
                unit = "интенсивность",
                category = SliderCategory.HABITS,
                icon = habitType.icon
            )
        }
    }
    
    fun createLifestyleSliders(currentFactors: Map<String, Double>): List<SliderConfig> {
        return listOf(
            SliderConfig(
                key = "lifestyle_physical_activity",
                title = "Физическая активность",
                description = "Регулярность упражнений и активности",
                minValue = 0.0,
                maxValue = 1.0,
                currentValue = currentFactors["physical_activity"] ?: 0.5,
                step = 0.05,
                unit = "уровень",
                category = SliderCategory.LIFESTYLE,
                icon = "💪"
            ),
            SliderConfig(
                key = "lifestyle_sleep_quality",
                title = "Качество сна",
                description = "Продолжительность и качество ночного отдыха",
                minValue = 0.0,
                maxValue = 1.0,
                currentValue = currentFactors["sleep_quality"] ?: 0.7,
                step = 0.05,
                unit = "качество",
                category = SliderCategory.LIFESTYLE,
                icon = "😴"
            ),
            SliderConfig(
                key = "lifestyle_stress_management",
                title = "Управление стрессом",
                description = "Способность справляться с стрессовыми ситуациями",
                minValue = 0.0,
                maxValue = 1.0,
                currentValue = currentFactors["stress_management"] ?: 0.5,
                step = 0.05,
                unit = "навык",
                category = SliderCategory.LIFESTYLE,
                icon = "🧘"
            ),
            SliderConfig(
                key = "lifestyle_diet_quality",
                title = "Качество питания",
                description = "Сбалансированность и полезность рациона",
                minValue = 0.0,
                maxValue = 1.0,
                currentValue = currentFactors["diet_quality"] ?: 0.6,
                step = 0.05,
                unit = "качество",
                category = SliderCategory.LIFESTYLE,
                icon = "🥗"
            ),
            SliderConfig(
                key = "lifestyle_social_connections",
                title = "Социальные связи",
                description = "Качество отношений с семьей и друзьями",
                minValue = 0.0,
                maxValue = 1.0,
                currentValue = currentFactors["social_connections"] ?: 0.8,
                step = 0.05,
                unit = "качество",
                category = SliderCategory.LIFESTYLE,
                icon = "👥"
            )
        )
    }
}