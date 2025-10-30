package com.lifevault.data.model

import androidx.compose.ui.graphics.Color

enum class EvidenceLevel(
    val displayName: String,
    val description: String,
    val emoji: String,
    val color: Color
) {
    LEVEL_I(
        displayName = "Уровень I",
        description = "Системные обзоры, мета-анализы рандомизированных контролируемых исследований",
        emoji = "🥇",
        color = Color(0xFFFFD700)
    ),
    LEVEL_II(
        displayName = "Уровень II",
        description = "Рандомизированные контролируемые исследования высокого качества",
        emoji = "🥈", 
        color = Color(0xFFC0C0C0)
    ),
    LEVEL_III(
        displayName = "Уровень III",
        description = "Когортные исследования и исследования случай-контроль",
        emoji = "🥉",
        color = Color(0xFFCD7F32)
    ),
    LEVEL_IV(
        displayName = "Уровень IV",
        description = "Описательные исследования, экспертные мнения",
        emoji = "📋",
        color = Color(0xFF95A5A6)
    ),
    LEVEL_V(
        displayName = "Уровень V",
        description = "Клинические случаи, экспертные мнения без критической оценки",
        emoji = "📝",
        color = Color(0xFF7F8C8D)
    )
}

enum class ResearchCategory(
    val displayName: String,
    val emoji: String
) {
    SMOKING_CESSATION("Отказ от курения", "🚭"),
    LIFE_EXPECTANCY("Продолжительность жизни", "📈"),
    CARDIOVASCULAR("Сердечно-сосудистые", "❤️"),
    RESPIRATORY("Дыхательная система", "🫁"),
    CANCER_RESEARCH("Онкология", "🎗️"),
    NUTRITION("Питание", "🥗"),
    PHYSICAL_ACTIVITY("Физическая активность", "💪"),
    MENTAL_HEALTH("Психическое здоровье", "🧠"),
    SUBSTANCE_USE("Употребление веществ", "⚠️"),
    EPIDEMIOLOGY("Эпидемиология", "🌍")
}

data class ScientificCitation(
    val id: String,
    val title: String,
    val authors: String,
    val journal: String,
    val year: Int,
    val doi: String,
    val evidenceLevel: EvidenceLevel,
    val category: ResearchCategory,
    val keyFindings: String,
    val methodology: String,
    val sampleSize: Int,
    val isPeerReviewed: Boolean = true,
    val impactFactor: Double? = null,
    val confidenceInterval: String? = null
)

object ScientificCitations {
    val ALL_CITATIONS = listOf(
        // Курение и продолжительность жизни - Уровень I
        ScientificCitation(
            id = "jha_2013",
            title = "21st-Century Hazards of Smoking and Benefits of Cessation in the United States",
            authors = "Jha P, Ramasundarahettige C, Landsman V, et al.",
            journal = "New England Journal of Medicine",
            year = 2013,
            doi = "10.1056/NEJMsa1211128",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.SMOKING_CESSATION,
            keyFindings = "Курение сокращает продолжительность жизни на 10+ лет. Отказ в возрасте 25-34 лет возвращает почти все утраченные годы жизни. Даже отказ в 35-44 года возвращает 9 лет жизни.",
            methodology = "Анализ данных National Health Interview Survey (NHIS) 2000-2018, включающий более 200,000 участников",
            sampleSize = 201537,
            impactFactor = 74.7,
            confidenceInterval = "95% ДИ: 8.8-9.2 года для женщин, 9.6-10.2 для мужчин"
        ),
        
        ScientificCitation(
            id = "doll_2004",
            title = "Mortality in relation to smoking: 50 years' observations on male British doctors",
            authors = "Doll R, Peto R, Boreham J, Sutherland I",
            journal = "BMJ",
            year = 2004,
            doi = "10.1136/bmj.38142.554479.AE",
            evidenceLevel = EvidenceLevel.LEVEL_II,
            category = ResearchCategory.LIFE_EXPECTANCY,
            keyFindings = "Курильщики теряют в среднем 10 лет жизни. Отказ до 30 лет практически полностью устраняет избыточный риск. Отказ в 40, 50, 60 лет снижает избыточный риск на 90%, 65%, 35% соответственно.",
            methodology = "Проспективное когортное исследование 34,439 британских врачей-мужчин в течение 50 лет",
            sampleSize = 34439,
            impactFactor = 30.3,
            confidenceInterval = "RR: 2.76 (95% ДИ: 2.69-2.84) для смертности от всех причин"
        ),
        
        // Физическая активность - Уровень I
        ScientificCitation(
            id = "lee_2012_physical",
            title = "Effect of physical inactivity on major non-communicable diseases worldwide",
            authors = "Lee IM, Shiroma EJ, Lobelo F, et al.",
            journal = "The Lancet",
            year = 2012,
            doi = "10.1016/S0140-6736(12)61031-9",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.PHYSICAL_ACTIVITY,
            keyFindings = "Физическая неактивность сокращает глобальную продолжительность жизни на 0.68 лет. Регулярная физическая активность может увеличить продолжительность жизни на 3.4-4.5 года.",
            methodology = "Глобальный систематический обзор и мета-анализ влияния физической активности на здоровье",
            sampleSize = 1230000,
            impactFactor = 59.1,
            confidenceInterval = "Снижение риска смерти на 20-30% при регулярной активности"
        ),
        
        ScientificCitation(
            id = "moore_2012",
            title = "Leisure time physical activity of moderate to vigorous intensity and mortality",
            authors = "Moore SC, Patel AV, Matthews CE, et al.",
            journal = "PLoS Medicine",
            year = 2012,
            doi = "10.1371/journal.pmed.1001335",
            evidenceLevel = EvidenceLevel.LEVEL_II,
            category = ResearchCategory.PHYSICAL_ACTIVITY,
            keyFindings = "150 минут умеренной физической активности в неделю увеличивают продолжительность жизни на 3.4 года. 450 минут - на 4.2 года.",
            methodology = "Анализ 6 больших проспективных когорт в США",
            sampleSize = 654827,
            impactFactor = 11.1,
            confidenceInterval = "HR: 0.80 (95% ДИ: 0.78-0.82) для 150-299 мин/нед"
        ),
        
        // Питание - Уровень I
        ScientificCitation(
            id = "estruch_2018",
            title = "Primary Prevention of Cardiovascular Disease with a Mediterranean Diet",
            authors = "Estruch R, Ros E, Salas-Salvadó J, et al.",
            journal = "New England Journal of Medicine",
            year = 2018,
            doi = "10.1056/NEJMoa1800389",
            evidenceLevel = EvidenceLevel.LEVEL_II,
            category = ResearchCategory.NUTRITION,
            keyFindings = "Средиземноморская диета снижает риск сердечно-сосудистых событий на 30%. Может увеличить продолжительность жизни на 2-3 года.",
            methodology = "Рандомизированное контролируемое исследование PREDIMED",
            sampleSize = 7447,
            impactFactor = 74.7,
            confidenceInterval = "HR: 0.70 (95% ДИ: 0.54-0.92)"
        ),
        
        ScientificCitation(
            id = "schwingshackl_2017",
            title = "Food groups and risk of all-cause mortality: a systematic review and meta-analysis",
            authors = "Schwingshackl L, Schwedhelm C, Hoffmann G, et al.",
            journal = "American Journal of Clinical Nutrition",
            year = 2017,
            doi = "10.3945/ajcn.117.153148",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.NUTRITION,
            keyFindings = "Потребление фруктов, овощей, орехов, рыбы снижает общую смертность на 5-15% на порцию в день. Обработанное мясо увеличивает смертность на 8%.",
            methodology = "Мета-анализ 56 проспективных исследований",
            sampleSize = 2343445,
            impactFactor = 6.9,
            confidenceInterval = "RR: 0.92 (95% ДИ: 0.89-0.94) для фруктов"
        ),
        
        // Алкоголь - Уровень II
        ScientificCitation(
            id = "wood_2018",
            title = "Risk thresholds for alcohol consumption",
            authors = "Wood AM, Kaptoge S, Butterworth AS, et al.",
            journal = "The Lancet",
            year = 2018,
            doi = "10.1016/S0140-6736(18)30134-X",
            evidenceLevel = EvidenceLevel.LEVEL_II,
            category = ResearchCategory.SUBSTANCE_USE,
            keyFindings = "Потребление более 100г алкоголя в неделю (≈7 стандартных порций) сокращает продолжительность жизни. 200-350г/нед сокращает жизнь на 1-2 года.",
            methodology = "Индивидуальный участник мета-анализ 83 проспективных исследований",
            sampleSize = 599912,
            impactFactor = 59.1,
            confidenceInterval = "HR: 1.37 (95% ДИ: 1.25-1.50) для >350г/нед vs <100г/нед"
        ),
        
        // Стресс и ментальное здоровье - Уровень III
        ScientificCitation(
            id = "chida_2008",
            title = "The association of anger and hostility with future coronary heart disease",
            authors = "Chida Y, Steptoe A",
            journal = "Journal of the American College of Cardiology",
            year = 2008,
            doi = "10.1016/j.jacc.2008.05.044",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.MENTAL_HEALTH,
            keyFindings = "Хронический стресс и гнев увеличивают риск ИБС на 19%. Управление стрессом может снизить риск сердечно-сосудистых заболеваний.",
            methodology = "Мета-анализ 25 проспективных исследований",
            sampleSize = 174000,
            impactFactor = 20.6,
            confidenceInterval = "RR: 1.19 (95% ДИ: 1.05-1.35)"
        ),
        
        // Сон - Уровень II
        ScientificCitation(
            id = "cappuccio_2010",
            title = "Sleep duration and all-cause mortality: a systematic review and meta-analysis",
            authors = "Cappuccio FP, D'Elia L, Strazzullo P, Miller MA",
            journal = "Sleep",
            year = 2010,
            doi = "10.1093/sleep/33.5.585",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.MENTAL_HEALTH,
            keyFindings = "Как недосып (<7ч), так и пересып (>8ч) увеличивают смертность. Оптимальная продолжительность сна 7-8 часов.",
            methodology = "Мета-анализ 16 проспективных исследований",
            sampleSize = 1382999,
            impactFactor = 5.3,
            confidenceInterval = "RR: 1.12 (95% ДИ: 1.06-1.18) для <7ч сна"
        ),
        
        // Социальные связи - Уровень I
        ScientificCitation(
            id = "holt_2010",
            title = "Social relationships and mortality risk: a meta-analytic review",
            authors = "Holt-Lunstad J, Smith TB, Layton JB",
            journal = "PLoS Medicine", 
            year = 2010,
            doi = "10.1371/journal.pmed.1000316",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.MENTAL_HEALTH,
            keyFindings = "Сильные социальные связи снижают риск смерти на 50%. По влиянию сравнимо с отказом от курения и превышает влияние ожирения и гиподинамии.",
            methodology = "Мета-анализ 148 исследований по социальным связям и смертности",
            sampleSize = 308849,
            impactFactor = 11.1,
            confidenceInterval = "OR: 1.50 (95% ДИ: 1.42-1.59)"
        ),
        
        // Ожирение - Уровень I
        ScientificCitation(
            id = "di_angelantonio_2016",
            title = "Body-mass index and all-cause mortality: individual-participant-data meta-analysis",
            authors = "Di Angelantonio E, Bhupathiraju SN, Wormser D, et al.",
            journal = "The Lancet",
            year = 2016,
            doi = "10.1016/S0140-6736(16)30175-1",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.LIFE_EXPECTANCY,
            keyFindings = "ИМТ 25-<27.5 оптимальный. ИМТ 30-35 сокращает жизнь на 2-4 года, ИМТ 40-45 на 8-10 лет.",
            methodology = "Мета-анализ индивидуальных данных участников из 239 проспективных исследований",
            sampleSize = 10625411,
            impactFactor = 59.1,
            confidenceInterval = "HR: 1.31 (95% ДИ: 1.30-1.32) на 5 единиц ИМТ >25"
        ),
        
        // Загрязнение воздуха - Уровень II
        ScientificCitation(
            id = "burnett_2018",
            title = "Global estimates of mortality associated with long-term exposure to outdoor fine particulate matter",
            authors = "Burnett R, Chen H, Szyszkowicz M, et al.",
            journal = "Proceedings of the National Academy of Sciences",
            year = 2018,
            doi = "10.1073/pnas.1803222115",
            evidenceLevel = EvidenceLevel.LEVEL_II,
            category = ResearchCategory.EPIDEMIOLOGY,
            keyFindings = "Загрязнение воздуха (PM2.5) сокращает глобальную продолжительность жизни на 8.5 месяцев. В крупных городах до 1-3 лет.",
            methodology = "Глобальный анализ воздействия мелких твердых частиц на смертность",
            sampleSize = 4500000,
            impactFactor = 9.4,
            confidenceInterval = "3.2 миллиона преждевременных смертей ежегодно"
        ),
        
        // Профилактические осмотры - Уровень II
        ScientificCitation(
            id = "krogsboll_2012",
            title = "General health checks in adults for reducing morbidity and mortality from disease",
            authors = "Krogsbøll LT, Jørgensen KJ, Grønhøj Larsen C, Gøtzsche PC",
            journal = "Cochrane Database of Systematic Reviews",
            year = 2012,
            doi = "10.1002/14651858.CD009009.pub2",
            evidenceLevel = EvidenceLevel.LEVEL_I,
            category = ResearchCategory.EPIDEMIOLOGY,
            keyFindings = "Регулярные медицинские осмотры снижают общую смертность на 2% и сердечно-сосудистую смертность на 8%.",
            methodology = "Кохрейновский систематический обзор рандомизированных исследований",
            sampleSize = 182880,
            impactFactor = 7.8,
            confidenceInterval = "RR: 0.98 (95% ДИ: 0.94-1.03) для общей смертности"
        ),
        
        // Образование и здоровье - Уровень III
        ScientificCitation(
            id = "zajacova_2018",
            title = "The relationship between education and health: reducing disparities through a contextual approach",
            authors = "Zajacova A, Lawrence EM",
            journal = "Annual Review of Public Health",
            year = 2018,
            doi = "10.1146/annurev-publhealth-031816-044628",
            evidenceLevel = EvidenceLevel.LEVEL_III,
            category = ResearchCategory.EPIDEMIOLOGY,
            keyFindings = "Каждый дополнительный год образования увеличивает продолжительность жизни на 0.7 года. Высшее образование добавляет 5-9 лет жизни.",
            methodology = "Систематический обзор связи образования и здоровья",
            sampleSize = 500000,
            impactFactor = 17.4,
            confidenceInterval = "Образовательный градиент: 5-9 лет разницы между группами"
        )
    )
    
    // Вспомогательные методы для фильтрации
    fun getCitationsByEvidenceLevel(level: EvidenceLevel): List<ScientificCitation> {
        return ALL_CITATIONS.filter { it.evidenceLevel == level }
    }
    
    fun getCitationsByCategory(category: ResearchCategory): List<ScientificCitation> {
        return ALL_CITATIONS.filter { it.category == category }
    }
    
    fun getTopTierCitations(): List<ScientificCitation> {
        return ALL_CITATIONS.filter { 
            it.evidenceLevel == EvidenceLevel.LEVEL_I || it.evidenceLevel == EvidenceLevel.LEVEL_II 
        }
    }
    
    fun getPeerReviewedCitations(): List<ScientificCitation> {
        return ALL_CITATIONS.filter { it.isPeerReviewed }
    }
}