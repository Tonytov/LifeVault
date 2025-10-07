package com.quitsmoking.data.model

data class Article(
    val id: Int,
    val title: String,
    val summary: String,
    val content: String,
    val category: ArticleCategory,
    val scientificLink: String,
    val imageEmoji: String,
    val readTimeMinutes: Int,
    val researchCitations: List<ResearchCitation> = emptyList(),
    val keyFindings: List<String> = emptyList(),
    val practicalTips: List<String> = emptyList(),
    val relatedHabits: List<HabitType> = emptyList(),
    val credibilityScore: Double = 0.8, // 0-1 оценка достоверности
    val difficulty: ArticleDifficulty = ArticleDifficulty.BEGINNER
)

data class ResearchCitation(
    val authors: List<String>,
    val title: String,
    val journal: String,
    val year: Int,
    val doi: String? = null,
    val pubmedId: String? = null,
    val url: String? = null,
    val studyType: StudyType,
    val participantCount: Int? = null,
    val followUpPeriod: String? = null, // например, "10 лет"
    val keyFinding: String,
    val evidenceLevel: EvidenceLevel
)

enum class StudyType(val displayName: String) {
    META_ANALYSIS("Мета-анализ"),
    SYSTEMATIC_REVIEW("Систематический обзор"),
    RANDOMIZED_CONTROLLED_TRIAL("Рандомизированное контролируемое исследование"),
    COHORT_STUDY("Когортное исследование"),
    CASE_CONTROL_STUDY("Исследование случай-контроль"),
    CROSS_SECTIONAL("Поперечное исследование"),
    OBSERVATIONAL("Наблюдательное исследование")
}

// EvidenceLevel moved to ScientificCitation.kt to avoid duplication
// Using the comprehensive version from there

enum class ArticleDifficulty(val displayName: String, val emoji: String) {
    BEGINNER("Для начинающих", "🌱"),
    INTERMEDIATE("Средний уровень", "📚"),
    ADVANCED("Продвинутый", "🎓"),
    EXPERT("Экспертный", "🔬")
}

enum class ArticleCategory(val displayName: String, val emoji: String) {
    SMOKING("Курение", "🚬"),
    ALCOHOL("Алкоголь", "🍺"),
    SUGAR("Сахар", "🍭")
}

object ArticlesRepository {
    val articles = listOf(
        // Статьи о курении
        Article(
            id = 1,
            title = "Как курение влияет на сердечно-сосудистую систему",
            summary = "Исследование показывает, что курение увеличивает риск сердечных заболеваний на 70%",
            content = "Курение табака является одним из основных факторов риска развития сердечно-сосудистых заболеваний. Никотин и другие химические вещества в табачном дыме повреждают кровеносные сосуды, увеличивают частоту сердечных сокращений и повышают кровяное давление.\n\nИсследования показывают, что у курящих людей риск развития ишемической болезни сердца в 2-4 раза выше, чем у некурящих. Кроме того, курение способствует образованию тромбов и атеросклеротических бляшек в артериях.",
            category = ArticleCategory.SMOKING,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/28336881/",
            imageEmoji = "❤️",
            readTimeMinutes = 5,
            researchCitations = listOf(
                ResearchCitation(
                    authors = listOf("Jha P.", "Ramasundarahettige C.", "Landsman V."),
                    title = "21st-century hazards of smoking and benefits of cessation in the United States",
                    journal = "New England Journal of Medicine",
                    year = 2013,
                    doi = "10.1056/NEJMsa1211128",
                    pubmedId = "23343063",
                    studyType = StudyType.COHORT_STUDY,
                    participantCount = 200000,
                    followUpPeriod = "15 лет",
                    keyFinding = "Курение уменьшает продолжительность жизни на 10+ лет",
                    evidenceLevel = com.quitsmoking.data.model.EvidenceLevel.LEVEL_II
                )
            ),
            keyFindings = listOf(
                "Курение увеличивает риск ИБС в 2-4 раза",
                "Каждая сигарета уменьшает жизнь на 11 минут",
                "Пассивное курение увеличивает риск сердечных заболеваний на 25-30%"
            ),
            practicalTips = listOf(
                "Начните с никотинозаместительной терапии",
                "Избегайте триггеров в первые недели",
                "Увеличьте физическую активность для борьбы со стрессом",
                "Обратитесь к врачу за поддержкой"
            ),
            relatedHabits = listOf(HabitType.SMOKING),
            credibilityScore = 0.95,
            difficulty = ArticleDifficulty.BEGINNER
        ),
        Article(
            id = 2,
            title = "Восстановление легких после отказа от курения",
            summary = "Легкие начинают восстанавливаться уже через 20 минут после последней сигареты",
            content = "Отказ от курения запускает немедленные процессы восстановления в организме. Через 20 минут нормализуется частота сердечных сокращений и кровяное давление. Через 12 часов уровень угарного газа в крови снижается до нормы.\n\nЧерез 2-12 недель улучшается кровообращение и функция легких. Через 1-9 месяцев уменьшается кашель и одышка. Реснички в легких восстанавливают свою функцию, улучшая очищение легких от слизи.",
            category = ArticleCategory.SMOKING,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/25895977/",
            imageEmoji = "🫁",
            readTimeMinutes = 4
        ),
        Article(
            id = 3,
            title = "Экономический ущерб от курения",
            summary = "Средний курильщик тратит более 100 000 рублей в год на сигареты",
            content = "Помимо вреда для здоровья, курение наносит серьезный экономический ущерб. При цене пачки 200 рублей и потреблении одной пачки в день, годовые расходы составляют более 73 000 рублей.\n\nК этому добавляются расходы на медицинское обслуживание, которые у курящих людей в среднем на 40% выше. Также снижается производительность труда из-за частых перерывов на курение и больничных.",
            category = ArticleCategory.SMOKING,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/24342558/",
            imageEmoji = "💰",
            readTimeMinutes = 3
        ),
        
        // Статьи об алкоголе
        Article(
            id = 4,
            title = "Влияние алкоголя на мозг и память",
            summary = "Даже умеренное употребление алкоголя может влиять на когнитивные функции",
            content = "Алкоголь оказывает прямое воздействие на центральную нервную систему. Этанол нарушает передачу нервных импульсов, влияет на нейромедиаторы и может вызывать структурные изменения в мозге.\n\nДлительное употребление алкоголя связано с ухудшением памяти, снижением способности к концентрации и повышенным риском развития деменции. Исследования показывают, что даже умеренные дозы могут влиять на качество сна и восстановительные процессы мозга.",
            category = ArticleCategory.ALCOHOL,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/23796475/",
            imageEmoji = "🧠",
            readTimeMinutes = 6
        ),
        Article(
            id = 5,
            title = "Алкоголь и заболевания печени",
            summary = "40% случаев цирроза печени связано с употреблением алкоголя",
            content = "Печень метаболизирует около 95% потребляемого алкоголя. При регулярном употреблении алкоголя клетки печени повреждаются токсичными продуктами распада этанола.\n\nСначала развивается жировая дистрофия печени, затем алкогольный гепатит, и в конечном итоге цирроз. По данным ВОЗ, алкогольная болезнь печени является причиной смерти более 3 миллионов человек в год по всему миру.",
            category = ArticleCategory.ALCOHOL,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/31985404/",
            imageEmoji = "🫄",
            readTimeMinutes = 5
        ),
        Article(
            id = 6,
            title = "Безопасные дозы алкоголя: миф или реальность",
            summary = "Последние исследования ставят под сомнение концепцию 'безопасных доз'",
            content = "Традиционно считалось, что небольшие дозы алкоголя могут быть безвредными или даже полезными. Однако крупномасштабное исследование 2018 года, опубликованное в The Lancet, показало, что безопасного уровня потребления алкоголя не существует.\n\nИсследование охватило данные 28 миллионов человек из 195 стран и показало, что риски для здоровья начинаются с первого глотка. Даже одна порция алкоголя в день увеличивает риск развития 23 связанных с алкоголем заболеваний.",
            category = ArticleCategory.ALCOHOL,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/30146330/",
            imageEmoji = "⚖️",
            readTimeMinutes = 7
        ),
        
        // Статьи о сахаре
        Article(
            id = 7,
            title = "Скрытый сахар в продуктах питания",
            summary = "В среднем человек потребляет в 3 раза больше сахара, чем рекомендуется",
            content = "ВОЗ рекомендует ограничить потребление свободных сахаров до 10% от общей калорийности рациона (около 50 г в день). Однако большинство людей потребляют значительно больше из-за скрытых сахаров в обработанных продуктах.\n\nСахар добавляется в хлеб, соусы, йогурты, мясные изделия и другие неожиданные продукты. Одна банка газировки может содержать до 40 г сахара - почти суточную норму.",
            category = ArticleCategory.SUGAR,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/26376619/",
            imageEmoji = "🔍",
            readTimeMinutes = 4
        ),
        Article(
            id = 8,
            title = "Сахар и развитие диабета 2 типа",
            summary = "Высокое потребление сахара увеличивает риск диабета на 26%",
            content = "Избыточное потребление сахара приводит к развитию инсулинорезистентности - состоянию, при котором клетки перестают нормально реагировать на инсулин. Это основной механизм развития диабета 2 типа.\n\nМета-анализ 17 исследований показал, что каждая дополнительная порция сладких напитков в день увеличивает риск развития диабета 2 типа на 26%. Особенно опасны напитки с высоким содержанием фруктозы.",
            category = ArticleCategory.SUGAR,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/23990623/",
            imageEmoji = "💉",
            readTimeMinutes = 5
        ),
        Article(
            id = 9,
            title = "Влияние сахара на старение кожи",
            summary = "Процесс гликации ускоряет появление морщин и потерю эластичности кожи",
            content = "Избыток сахара в крови запускает процесс гликации - неферментативное присоединение сахаров к белкам. Это особенно влияет на коллаген и эластин - белки, отвечающие за упругость и молодость кожи.\n\nГликированные белки образуют AGEs (конечные продукты гликирования), которые делают кожу менее эластичной и более склонной к образованию морщин. Исследования показывают, что снижение потребления сахара может замедлить процессы старения кожи.",
            category = ArticleCategory.SUGAR,
            scientificLink = "https://pubmed.ncbi.nlm.nih.gov/21457365/",
            imageEmoji = "✨",
            readTimeMinutes = 4
        )
    )
    
    fun getArticlesByCategory(category: ArticleCategory): List<Article> {
        return articles.filter { it.category == category }
    }
    
    fun getAllCategories(): List<ArticleCategory> {
        return ArticleCategory.values().toList()
    }
}