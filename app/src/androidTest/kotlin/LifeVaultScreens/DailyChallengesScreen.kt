package LifeVaultScreens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTestTag

class DailyChallengesScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DailyChallengesScreen>(
        semanticsProvider = semanticsProvider
    ) {

    // Заголовок экрана
    val screenTitle = child<KNode> {
        hasTestTag("challengesScreenTitle")
    }

    // Главный список челленджей
    val challengesList = child<KNode> {
        hasTestTag("challengesList")
    }

    // Карточка статистики пользователя
    val userStatsCard = child<KNode> {
        hasTestTag("userStatsCard")
    }

    // Фильтры по категориям
    val categoryFilters = child<KNode> {
        hasTestTag("categoryFilters")
    }

    // Фильтр "Все категории"
    val filterCategoryAll = child<KNode> {
        hasTestTag("filterCategory_all")
    }

    // Фильтр категории "Отказ от привычек"
    val filterCategoryQuitHabits = child<KNode> {
        hasTestTag("filterCategory_QUIT_HABITS")
    }

    // Фильтр категории "Здоровый образ жизни"
    val filterCategoryHealthyLifestyle = child<KNode> {
        hasTestTag("filterCategory_HEALTHY_LIFESTYLE")
    }

    // Фильтр категории "Питание"
    val filterCategoryNutrition = child<KNode> {
        hasTestTag("filterCategory_NUTRITION")
    }

    // Фильтры по сложности
    val difficultyFilters = child<KNode> {
        hasTestTag("difficultyFilters")
    }

    // Фильтр "Любая сложность"
    val filterDifficultyAll = child<KNode> {
        hasTestTag("filterDifficulty_all")
    }

    // Фильтр сложности "Новичок"
    val filterDifficultyBeginner = child<KNode> {
        hasTestTag("filterDifficulty_BEGINNER")
    }

    // Фильтр сложности "Средний"
    val filterDifficultyIntermediate = child<KNode> {
        hasTestTag("filterDifficulty_INTERMEDIATE")
    }

    // Фильтр сложности "Продвинутый"
    val filterDifficultyAdvanced = child<KNode> {
        hasTestTag("filterDifficulty_ADVANCED")
    }

    // Челлендж дня
    val challengeOfTheDayCard = child<KNode> {
        hasTestTag("challengeOfTheDayCard")
    }

    // Текст "Доступные вызовы"
    val availableChallengesTitle = child<KNode> {
        hasText("📋 Доступные вызовы")
    }

    // Текст "Активные вызовы"
    val activeChallengesTitle = child<KNode> {
        hasText("🔥 Активные вызовы")
    }

    // Элементы статистики
    val statItemStreak = child<KNode> {
        hasTestTag("statItem_streak")
    }

    val statItemSuccess = child<KNode> {
        hasTestTag("statItem_success")
    }

    val statItemRecord = child<KNode> {
        hasTestTag("statItem_record")
    }

    val statItemEarned = child<KNode> {
        hasTestTag("statItem_earned")
    }

    // Методы для работы с конкретными челленджами
    fun challengeItem(id: Long) = child<KNode> {
        hasTestTag("challengeItem_$id")
    }

    fun startChallengeButton(id: Long) = child<KNode> {
        hasTestTag("startChallengeButton_$id")
    }

    fun activeChallengeCard(id: Long) = child<KNode> {
        hasTestTag("activeChallengeCard_$id")
    }

    // Метод для проверки наличия текста челленджа (deprecated - используйте challengeItem(id) вместо этого)
    fun challengeWithText(text: String) = child<KNode> {
        hasText(text)
    }
}
