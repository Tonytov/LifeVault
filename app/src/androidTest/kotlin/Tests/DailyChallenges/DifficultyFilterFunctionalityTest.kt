package Tests.DailyChallenges

import LifeVaultScreens.DailyChallengesScreen
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import com.lifevault.presentation.ui.challenges.DailyChallengesActivity
import org.junit.Rule
import org.junit.Test

/**
 * Тест для проверки функциональности фильтрации по сложности
 */
class DifficultyFilterFunctionalityTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DailyChallengesActivity>()

    @Test
    fun testDifficultyFilterFunctionality() = run {
        step("Проверяем отображение фильтров сложности") {
            flakySafely(timeoutMs = 5000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    difficultyFilters.assertExists()
                    filterDifficultyAll.assertExists()
                    filterDifficultyBeginner.assertExists()
                }
            }
        }

        step("Применяем фильтр 'Новичок'") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                filterDifficultyBeginner.performClick()
            }
        }

        step("Проверяем наличие челленджей для новичков") {
            flakySafely(timeoutMs = 5000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    challengeWithText("День без сигарет").assertExists()
                }
            }
        }

        step("Применяем фильтр 'Средний'") {
            flakySafely(timeoutMs = 5000) {
                // Прокручиваем LazyRow до нужного элемента
                composeTestRule.onNodeWithTag("difficultyFilters")
                    .performScrollToNode(hasTestTag("filterDifficulty_INTERMEDIATE"))

                // Кликаем на фильтр
                composeTestRule.onNodeWithTag("filterDifficulty_INTERMEDIATE")
                    .performClick()
            }
        }

        step("Проверяем наличие челленджей средней сложности") {
            flakySafely(timeoutMs = 5000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    challengeWithText("Неделя без курения").assertExists()
                }
            }
        }
    }
}
