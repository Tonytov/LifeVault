package Tests.DailyChallenges

import LifeVaultScreens.DailyChallengesScreen
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import com.lifevault.presentation.ui.challenges.DailyChallengesActivity
import org.junit.Rule
import org.junit.Test

/**
 * Тест для проверки отображения списка челленджей
 */
class ChallengesListTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DailyChallengesActivity>()

    @Test
    fun testChallengesList() = run {
        step("Проверяем отображение списка челленджей") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                challengesList.assertIsDisplayed()
            }
        }

        step("Проверяем наличие конкретных челленджей") {
            flakySafely(timeoutMs = 5000) {
                // Прокручиваем до первого челленджа
                composeTestRule.onNodeWithTag("challengesList")
                    .performScrollToNode(hasText("День без сигарет", substring = true))

                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    challengeWithText("День без сигарет").assertExists()
                }
            }

            flakySafely(timeoutMs = 5000) {
                // Прокручиваем до второго челленджа
                composeTestRule.onNodeWithTag("challengesList")
                    .performScrollToNode(hasText("Трезвый день", substring = true))

                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    challengeWithText("Трезвый день").assertExists()
                }
            }

            flakySafely(timeoutMs = 5000) {
                // Прокручиваем до третьего челленджа
                composeTestRule.onNodeWithTag("challengesList")
                    .performScrollToNode(hasText("День без сладкого", substring = true))

                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    challengeWithText("День без сладкого").assertExists()
                }
            }
        }

        step("Проверяем отображение карточки 'Челлендж дня'") {
            flakySafely(timeoutMs = 5000) {
                // Прокручиваем к карточке "Челлендж дня" (она в конце списка)
                composeTestRule.onNodeWithTag("challengesList")
                    .performScrollToNode(hasTestTag("challengeOfTheDayCard"))

                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    challengeOfTheDayCard.assertExists()
                }
            }
        }
    }
}
