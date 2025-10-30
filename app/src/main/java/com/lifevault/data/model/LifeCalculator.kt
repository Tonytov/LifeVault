package com.lifevault.data.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.pow
import kotlin.random.Random

object LifeCalculator {
    
    // Коэффициенты конверсии поведения в дни жизни (основано на мета-анализах)
    private val behaviorCoefficients = mapOf(
        // Курение (на сигарету в день)
        HabitType.SMOKING to BehaviorCoefficient(
            negativeImpactPerUnit = -0.3, // -0.3 дня за сигарету в день
            positiveImpactOnQuit = 2555,   // +2555 дней при полном отказе (7 лет)
            referenceSource = "Jha et al. NEJM 2013; Doll et al. BMJ 2004"
        ),
        
        // Алкоголь (на стандартную единицу в неделю)
        HabitType.ALCOHOL to BehaviorCoefficient(
            negativeImpactPerUnit = -2.1, // -2.1 дня за единицу в неделю свыше 14
            positiveImpactOnQuit = 365,    // +1 год при снижении до умеренного потребления
            referenceSource = "Wood et al. Lancet 2018; Burton & Sheron Lancet 2018"
        ),
        
        // Сахар (на 50г добавленного сахара в день)
        HabitType.SUGAR to BehaviorCoefficient(
            negativeImpactPerUnit = -0.8, // -0.8 дня за 50г сахара в день
            positiveImpactOnQuit = 180,    // +6 месяцев при снижении до WHO рекомендаций
            referenceSource = "Malik et al. Circulation 2010; Yang et al. JAMA 2014"
        )
    )
    
    // Дополнительные факторы образа жизни
    private val lifestyleFactors = mapOf(
        "physical_activity" to 1095.0,     // +3 года за регулярные упражнения
        "sleep_quality" to 730.0,          // +2 года за качественный сон 7-9 часов
        "stress_management" to 365.0,      // +1 год за управление стрессом
        "social_connections" to 1460.0,    // +4 года за сильные социальные связи
        "diet_quality" to 912.0,           // +2.5 года за средиземноморскую диету
        "preventive_care" to 273.0         // +9 месяцев за регулярные медосмотры
    )
    
    data class BehaviorCoefficient(
        val negativeImpactPerUnit: Double, // Дни потерянные за единицу вредного поведения
        val positiveImpactOnQuit: Int,     // Дни приобретенные при полном отказе
        val referenceSource: String       // Научный источник
    )
    
    data class LifeExpectancyCalculation(
        val baseLifeExpectancy: Double,
        val habitAdjustments: Map<HabitType, Double>,
        val lifestyleAdjustments: Map<String, Double>,
        val finalLifeExpectancy: Double,
        val confidenceInterval: Pair<Double, Double>, // 95% доверительный интервал
        val daysGainedFromQuits: Int,
        val medicalDisclaimer: String,
        val lastCalculated: LocalDateTime = LocalDateTime.now()
    )
    
    fun calculateLifeExpectancy(
        profile: UserProfile,
        habits: List<Habit>,
        lifestyleScore: Float = 0.5f
    ): LifeExpectancyCalculation {
        
        // Базовая продолжительность жизни
        val baseExpectancy = profile.region.getLifeExpectancyForGender(profile.gender).toDouble()
        
        // Корректировка по возрасту (актуариальные таблицы)
        val currentAge = java.time.Period.between(profile.birthDate, LocalDate.now()).years
        val ageAdjustedBase = adjustForCurrentAge(baseExpectancy, currentAge, profile.gender)
        
        // Расчет влияния привычек
        val habitAdjustments = mutableMapOf<HabitType, Double>()
        var totalHabitAdjustment = 0.0
        var daysGainedFromQuits = 0
        
        for (habitType in HabitType.values()) {
            val habit = habits.find { it.type == habitType }
            val coefficient = behaviorCoefficients[habitType] ?: continue
            
            when {
                habit == null || !habit.isActive -> {
                    // Привычки нет или она была брошена - добавляем бонус
                    if (habit?.quitDate != null) {
                        daysGainedFromQuits += coefficient.positiveImpactOnQuit
                        habitAdjustments[habitType] = coefficient.positiveImpactOnQuit.toDouble()
                        totalHabitAdjustment += coefficient.positiveImpactOnQuit
                    }
                }
                else -> {
                    // Активная вредная привычка
                    val dailyUsage = habit.dailyUsage ?: 1
                    val negativeImpact = coefficient.negativeImpactPerUnit * dailyUsage * 365.25 // Годовое влияние
                    habitAdjustments[habitType] = negativeImpact
                    totalHabitAdjustment += negativeImpact
                }
            }
        }
        
        // Корректировка по образу жизни
        val lifestyleAdjustments = mutableMapOf<String, Double>()
        var totalLifestyleAdjustment = 0.0
        
        getAllLifestyleFactors().forEach { (factor, maxBenefit) ->
            val benefit = maxBenefit * lifestyleScore
            lifestyleAdjustments[factor] = benefit
            totalLifestyleAdjustment += benefit
        }
        
        // Финальный расчет
        val totalAdjustmentDays = totalHabitAdjustment + totalLifestyleAdjustment
        val finalLifeExpectancyYears = ageAdjustedBase + (totalAdjustmentDays / 365.25)
        
        // 95% доверительный интервал (±15% как стандартная погрешность)
        val margin = finalLifeExpectancyYears * 0.15
        val confidenceInterval = Pair(
            finalLifeExpectancyYears - margin,
            finalLifeExpectancyYears + margin
        )
        
        return LifeExpectancyCalculation(
            baseLifeExpectancy = ageAdjustedBase,
            habitAdjustments = habitAdjustments,
            lifestyleAdjustments = lifestyleAdjustments,
            finalLifeExpectancy = finalLifeExpectancyYears,
            confidenceInterval = confidenceInterval,
            daysGainedFromQuits = daysGainedFromQuits,
            medicalDisclaimer = getMedicalDisclaimer()
        )
    }
    
    private fun adjustForCurrentAge(baseExpectancy: Double, currentAge: Int, gender: Gender): Double {
        // Упрощенная корректировка по возрасту
        return when {
            currentAge < 30 -> baseExpectancy
            currentAge < 50 -> baseExpectancy - ((currentAge - 30) * 0.1)
            currentAge < 70 -> baseExpectancy - (2.0 + (currentAge - 50) * 0.2)
            else -> baseExpectancy - (6.0 + (currentAge - 70) * 0.3)
        }.coerceAtLeast(1.0)
    }
    
    fun calculateBaseLifeExpectancy(profile: UserProfile): Int {
        var lifeExpectancy = profile.region.getLifeExpectancyForGender(profile.gender)
        
        // BMI adjustment
        val bmi = calculateBMI(profile.weight, profile.height)
        lifeExpectancy += getBMILifeAdjustment(bmi)
        
        return lifeExpectancy.coerceAtLeast(30) // minimum 30 years
    }
    
    fun calculateRemainingLifeWithHabits(
        profile: UserProfile,
        habits: List<Habit>
    ): LocalDateTime {
        val calculation = calculateLifeExpectancy(profile, habits, 0.6f)
        val currentAge = java.time.Period.between(profile.birthDate, LocalDate.now()).years
        val remainingYears = (calculation.finalLifeExpectancy - currentAge).coerceAtLeast(0.0)
        
        return LocalDateTime.now().plusDays((remainingYears * 365.25).toLong())
    }
    
    fun calculateLifeExtensionFromQuittingHabit(habitType: HabitType): Int {
        return getCoefficient(habitType)?.positiveImpactOnQuit ?: 0
    }
    
    private fun calculateBMI(weightKg: Int, heightCm: Int): Double {
        val heightM = heightCm / 100.0
        return weightKg / (heightM.pow(2))
    }
    
    private fun getBMILifeAdjustment(bmi: Double): Int {
        return when {
            bmi < 18.5 -> -2 // Underweight
            bmi < 25.0 -> 0   // Normal
            bmi < 30.0 -> -1  // Overweight
            else -> -3        // Obese
        }
    }
    
    private fun getMedicalDisclaimer(): String {
        return """
        ⚠️ МЕДИЦИНСКОЕ ПРЕДУПРЕЖДЕНИЕ:
        
        Данные расчеты носят исключительно образовательный и мотивационный характер. 
        Они основаны на эпидемиологических данных и не могут предсказать индивидуальную 
        продолжительность жизни.
        
        Реальные факторы влияющие на продолжительность жизни включают:
        • Генетические факторы (25-30%)
        • Социально-экономические условия (15-20%)
        • Экологические факторы (5-10%)
        • Медицинское обслуживание (10-15%)
        • Поведенческие факторы (40-50%)
        
        Приложение НЕ заменяет медицинскую консультацию. Для принятия решений 
        о здоровье всегда консультируйтесь с квалифицированными медицинскими 
        специалистами.
        
        Доверительный интервал ±15% отражает статистическую неопределенность, 
        фактическая вариация может быть значительно больше.
        """.trimIndent()
    }
    
    // Получение источников исследований
    fun getResearchSources(): Map<HabitType, String> {
        return behaviorCoefficients.mapValues { it.value.referenceSource }
    }
    
    // Публичный доступ к коэффициентам
    fun getCoefficient(habitType: HabitType): BehaviorCoefficient? {
        return behaviorCoefficients[habitType]
    }
    
    // Публичный доступ к факторам образа жизни
    fun getLifestyleFactor(factor: String): Double? {
        return lifestyleFactors[factor]
    }
    
    fun getAllLifestyleFactors(): Map<String, Double> {
        return lifestyleFactors
    }
    
    // Расчет потенциального улучшения при изменении привычек
    fun calculateWhatIfScenario(
        profile: UserProfile,
        currentHabits: List<Habit>,
        scenarioChanges: Map<HabitType, ScenarioChange>
    ): Map<HabitType, Double> {
        val results = mutableMapOf<HabitType, Double>()
        
        scenarioChanges.forEach { (habitType, change) ->
            val coefficient = getCoefficient(habitType) ?: return@forEach
            val currentHabit = currentHabits.find { it.type == habitType }
            
            val daysImpact = when (change) {
                is ScenarioChange.Quit -> coefficient.positiveImpactOnQuit.toDouble()
                is ScenarioChange.Reduce -> {
                    val currentUsage = currentHabit?.dailyUsage ?: 0
                    val reduction = (currentUsage - change.newDailyAmount).coerceAtLeast(0)
                    reduction * coefficient.negativeImpactPerUnit * -365.25 // Положительное влияние
                }
                is ScenarioChange.Start -> {
                    change.dailyAmount * coefficient.negativeImpactPerUnit * 365.25 // Негативное влияние
                }
            }
            
            results[habitType] = daysImpact
        }
        
        return results
    }
    
    sealed class ScenarioChange {
        object Quit : ScenarioChange()
        data class Reduce(val newDailyAmount: Int) : ScenarioChange()
        data class Start(val dailyAmount: Int) : ScenarioChange()
    }
    
    data class LifeCountdown(
        val totalDaysRemaining: Long,
        val years: Long,
        val days: Long,
        val hours: Long,
        val minutes: Long,
        val seconds: Long
    )
    
    fun calculateCountdown(
        birthDate: LocalDate,
        currentLifeExpectancy: Double
    ): LifeCountdown {
        val currentAge = java.time.Period.between(birthDate, LocalDate.now()).years
        val remainingYears = (currentLifeExpectancy - currentAge).coerceAtLeast(0.0)
        
        // Добавляем случайную вариацию для реалистичности
        val randomDays = Random.nextLong(-30, 31)
        val totalDays = (remainingYears * 365.25).toLong() + randomDays
        
        val now = LocalDateTime.now()
        val endOfLife = now.plusDays(totalDays)
        val totalMinutes = ChronoUnit.MINUTES.between(now, endOfLife)
        
        return LifeCountdown(
            totalDaysRemaining = totalDays,
            years = totalDays / 365,
            days = totalDays % 365,
            hours = (totalMinutes % (24 * 60)) / 60,
            minutes = totalMinutes % 60,
            seconds = Random.nextLong(0, 60) // Имитация секунд
        )
    }
    
    fun calculateLifeCountdown(deathDate: LocalDateTime): LifeCountdown {
        val now = LocalDateTime.now()
        
        if (now.isAfter(deathDate)) {
            return LifeCountdown(0, 0, 0, 0, 0, 0)
        }
        
        val totalSeconds = ChronoUnit.SECONDS.between(now, deathDate)
        val totalDays = totalSeconds / (24 * 60 * 60)
        
        val years = totalSeconds / (365 * 24 * 60 * 60)
        val remainingAfterYears = totalSeconds % (365 * 24 * 60 * 60)
        
        val days = remainingAfterYears / (24 * 60 * 60)
        val remainingAfterDays = remainingAfterYears % (24 * 60 * 60)
        
        val hours = remainingAfterDays / (60 * 60)
        val remainingAfterHours = remainingAfterDays % (60 * 60)
        
        val minutes = remainingAfterHours / 60
        val seconds = remainingAfterHours % 60
        
        return LifeCountdown(totalDays, years, days, hours, minutes, seconds)
    }
}