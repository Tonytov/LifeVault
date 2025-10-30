package Scenarios

import LifeVaultScreens.DailyChallengesScreen
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen

class DailyChallengesScenario(
    private val composeTestRule: ComposeTestRule
) : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Проверяем что находимся на экране Ежедневных Вызовов") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
                challengesList.assertIsDisplayed()
            }
        }

        step("Проверяем отображение карточки статистики") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                userStatsCard.assertIsDisplayed()
            }
        }

        step("Проверяем отображение фильтров") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                categoryFilters.assertIsDisplayed()
                difficultyFilters.assertIsDisplayed()
            }
        }

        step("Проверяем отображение списка доступных челленджей") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                availableChallengesTitle.assertIsDisplayed()
            }
        }
    }
}
