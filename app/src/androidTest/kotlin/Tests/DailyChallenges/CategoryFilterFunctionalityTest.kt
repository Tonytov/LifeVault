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
 * Тест для проверки функциональности фильтрации по категориям
 */
class CategoryFilterFunctionalityTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DailyChallengesActivity>()

    @Test
    fun testCategoryFilterFunctionality() = run {
        step("Проверяем базовое отображение") {
            flakySafely(timeoutMs = 5000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    screenTitle.assertIsDisplayed()
                    challengesList.assertIsDisplayed()
                }
            }
        }

        step("Скроллим до фильтров и проверяем их наличие") {
            flakySafely(timeoutMs = 5000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    categoryFilters.assertExists()
                    filterCategoryAll.assertExists()
                }
            }
        }

        step("Применяем фильтр 'Отказ от привычек'") {
            flakySafely(timeoutMs = 4000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    filterCategoryQuitHabits.performClick()
                }
            }
        }

        step("Проверяем что челленджи отфильтрованы") {
            flakySafely(timeoutMs = 6000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    // Проверяем что челлендж из категории "Отказ от привычек" присутствует
                    challengeWithText("День без сигарет").assertExists()
                }
            }
        }

        step("Сбрасываем фильтр на 'Все'") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                filterCategoryAll.performClick()
            }
        }

        step("Применяем фильтр 'Питание'") {
            flakySafely(timeoutMs = 6000) {
                // Прокручиваем LazyRow до нужного элемента
                composeTestRule.onNodeWithTag("categoryFilters")
                    .performScrollToNode(hasTestTag("filterCategory_NUTRITION"))
                flakySafely(timeoutMs = 4000) {
                    // Кликаем на фильтр
                    composeTestRule.onNodeWithTag("filterCategory_NUTRITION")
                        .performClick()
                }
            }

            step("Проверяем наличие челленджей из категории 'Питание'") {
                flakySafely(timeoutMs = 7000) {
                    onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                        challengeWithText("День без сладкого").assertExists()
                    }
                }
            }
        }
    }
}
