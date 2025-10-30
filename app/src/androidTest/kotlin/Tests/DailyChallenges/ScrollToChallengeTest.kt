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
 * Тест для проверки скролла списка челленджей
 */
class ScrollToChallengeTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DailyChallengesActivity>()

    @Test
    fun testScrollToChallenge() = run {
        step("Проверяем отображение списка") {
            onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                challengesList.assertIsDisplayed()
            }
        }

        step("Скроллим список до конца") {
            flakySafely(timeoutMs = 5000) {
                // Прокручиваем к карточке "Челлендж дня" в конце списка
                composeTestRule.onNodeWithTag("challengesList")
                    .performScrollToNode(hasTestTag("challengeOfTheDayCard"))

                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    // Проверяем что можем найти элементы в конце списка
                    challengeOfTheDayCard.assertExists()
                }
            }
        }

        step("Проверяем наличие разных челленджей в списке") {
            val challenges = listOf(
                "День без сигарет",
                "Неделя без курения",
                "Трезвый день",
                "День без сладкого",
                "10,000 шагов"
            )

            // Прокручиваем до каждого челленджа и проверяем его наличие
            challenges.forEach { challengeText ->
                flakySafely(timeoutMs = 5000) {
                    composeTestRule.onNodeWithTag("challengesList")
                        .performScrollToNode(hasText(challengeText, substring = true))

                    onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                        challengeWithText(challengeText).assertExists()
                    }
                }
            }
        }
    }
}
